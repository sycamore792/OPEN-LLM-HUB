package org.sycamore.llmhub.core.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.sycamore.llmhub.core.ModelRequestCommand;
import org.sycamore.llmhub.core.client.ReactorNettyClient;
import org.sycamore.llmhub.core.model.openai.OpenAiChatUsageModel;
import org.sycamore.llmhub.infrastructure.common.OutApiResponseStrategyMarkEnum;
import org.sycamore.llmhub.core.event.ModelLogInsertEvent;
import org.sycamore.llmhub.core.model.openai.OpenAiChatRequestModel;
import org.sycamore.llmhub.core.model.openai.OpenAiChatResponseModel;
import org.sycamore.llmhub.core.model.openai.OpenAiStreamOptionModel;
import org.sycamore.llmhub.core.service.ModelServiceI;
import org.sycamore.llmhub.infrastructure.common.SseEmitter;

import org.sycamore.llmhub.infrastructure.dataobject.ModelLog;
import org.sycamore.llmhub.infrastructure.dto.resp.SelectModelServerInfoByKeyRespDTO;
import org.sycamore.llmhub.infrastructure.strategy.AbstractStrategyChoose;
import reactor.core.Disposable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: Sycamore
 * @date: 2024/7/19 15:58
 * @version: 1.0
 * @description: TODO
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelServiceI {
    private final ReactorNettyClient reactorNettyClient;
    private final AbstractStrategyChoose abstractStrategyChoose;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void chatCompletions(ModelRequestCommand command, SseEmitter sseEmitter) {
        // 通过 apiKey 与 model 加载配置
        SelectModelServerInfoByKeyRespDTO modelServerInfoRespDTO = command.getModelServerInfo();

        // load 请求配置 （缓存）
        String url = modelServerInfoRespDTO.getModelServerBaseUrl() + "/chat/completions";
        JSONObject reqParamsJson = JSONObject.parseObject(modelServerInfoRespDTO.getServerParams());
        Map<String, String> headerMap = new HashMap<>();
        JSONArray headers = reqParamsJson.getJSONArray("headers");
        headers.forEach(header -> {
            JSONObject headerJson = JSONObject.from(header);
            headerMap.put(headerJson.getString("key"), headerJson.getString("value"));
        });
        headerMap.put("Content-Type", "application/json");

        // 加载协议code
        Integer protocolCode = modelServerInfoRespDTO.getProtocolCode();


        // 请求体适配
        OpenAiChatRequestModel requestModel = command.getRequestModel();

        // todo 根据协议code加载convert策略 目前默认为openai（模型确实也都是接的openai协议的）所以直接字符串化

        OpenAiChatRequestModel requestModelCopy = new OpenAiChatRequestModel();
        BeanUtils.copyProperties(requestModel, requestModelCopy);

        requestModelCopy.setModel(modelServerInfoRespDTO.getModelServerName());
        if (requestModel.isStream()) {
            requestModelCopy.setStreamOption(new OpenAiStreamOptionModel());
        }
        String body = JSONObject.toJSONString(requestModelCopy);

        //响应体公共参数值
        String model = requestModel.getModel();
        long created = System.currentTimeMillis();
        String id = getChatCompletionId(command, created);
        log.info("chatCompletions, Authorization：[ {} ]  requestModel: {}", command.getApiKey(), requestModel);


        ModelLog modelLog = new ModelLog();
        modelLog.setId(id);
        modelLog.setApiKey(command.getApiKey());
        modelLog.setModelId(modelServerInfoRespDTO.getLlmModelId());
        JSONObject requestJson = new JSONObject();
        requestJson.put("origin", requestModel);
        requestJson.put("convert", requestModelCopy);
        modelLog.setRequestJson(requestJson.toJSONString());
        modelLog.setCreated(created);

        JSONObject responseJson = new JSONObject();
        JSONArray streamChunksOrigin = new JSONArray();
        JSONArray streamChunksConvert = new JSONArray();
        if (requestModel.isStream()) {
            responseJson.put("streamChunksOrigin", streamChunksOrigin);
            responseJson.put("streamChunksConvert", streamChunksConvert);
        }



        // 调用模型服务
        AtomicBoolean isFirstChunk = new AtomicBoolean(false);
        AtomicBoolean eventConsumeFlag = new AtomicBoolean(false);

        Disposable disposable = reactorNettyClient.sendRequest(
                requestModel.isStream(),
                url,
                body,
                headerMap,
                event -> {
                    if (!StringUtils.isBlank(event)) {
                        try {
                            if (!requestModel.isStream()) {
                                responseJson.put("noStreamOrigin", event);
                            } else {
                                streamChunksOrigin.add(event);
                            }
                            // 加载响应体适配策略
                            String mark = Objects.requireNonNull(OutApiResponseStrategyMarkEnum.getByCode(protocolCode)).getMark();
                            OpenAiChatResponseModel response = abstractStrategyChoose.chooseAndExecuteResp(mark, event);
                            if (Objects.nonNull(response)) {
                                //公共参数填充
                                response.setModel(model);
                                response.setId(id);
                                response.setCreated(created);
                                if (!isFirstChunk.compareAndExchange(false, true) && requestModel.isStream()){
                                    modelLog.setFirstChunkDelay( (System.currentTimeMillis() - created));
                                }
                                //todo
                                Optional.ofNullable(response.getUsage()).ifPresentOrElse(
                                        usage -> {
                                            Optional.ofNullable(usage.getPromptTokens()).ifPresent(promptTokens->modelLog.setPromptTokens(Long.valueOf(promptTokens)));
                                            Optional.ofNullable(usage.getCompletionTokens()).ifPresent(completionTokens->modelLog.setCompletionTokens(Long.valueOf(completionTokens)));
                                            Optional.ofNullable(usage.getTotalTokens()).ifPresent(totalTokens->modelLog.setTotalTokens(Long.valueOf(totalTokens)));
                                        },
                                        () -> {
                                            if (Objects.nonNull(response.getChoices())&& !response.getChoices().isEmpty() && Objects.nonNull(response.getChoices().get(0).getUsage())){
                                                Optional.ofNullable(response.getChoices().get(0).getUsage().getPromptTokens()).ifPresent(promptTokens->modelLog.setPromptTokens(Long.valueOf(promptTokens)));
                                                Optional.ofNullable(response.getChoices().get(0).getUsage().getCompletionTokens()).ifPresent(completionTokens->modelLog.setCompletionTokens(Long.valueOf(completionTokens)));
                                                Optional.ofNullable(response.getChoices().get(0).getUsage().getTotalTokens()).ifPresent(totalTokens->modelLog.setTotalTokens(Long.valueOf(totalTokens)));
//                                                modelLog.setPromptTokens(Long.valueOf(response.getChoices().get(0).getUsage().getPromptTokens()));
//                                                modelLog.setCompletionTokens(Long.valueOf(response.getChoices().get(0).getUsage().getCompletionTokens()));
//                                                modelLog.setTotalTokens(Long.valueOf(response.getChoices().get(0).getUsage().getTotalTokens()));
                                            }
                                        }
                                );
//                                if (Objects.nonNull(response.getUsage())) {
//
//                                    modelLog.setCompletionTokens(Long.valueOf(response.getUsage().getCompletionTokens()));
//                                    modelLog.setTotalTokens(Long.valueOf(response.getUsage().getTotalTokens()));
//                                }else if (Objects.nonNull(response.getChoices())&& !response.getChoices().isEmpty() && Objects.nonNull(response.getChoices().get(0).getUsage())){
//                                    modelLog.setPromptTokens(Long.valueOf(response.getChoices().get(0).getUsage().getPromptTokens()));
//                                    modelLog.setCompletionTokens(Long.valueOf(response.getChoices().get(0).getUsage().getCompletionTokens()));
//                                    modelLog.setTotalTokens(Long.valueOf(response.getChoices().get(0).getUsage().getTotalTokens()));
//
//                                }


                                if (requestModel.isStream()) {
                                    streamChunksConvert.add(response);

                                    sseEmitter.send(JSONObject.toJSONString(response));

                                    // 检查 response 和 choices 是否为空，choices 的长度是否大于 0
                                    if (response.isDoneFlag()) {
                                        if (Objects.nonNull(modelLog.getCompletionTokens())&& modelLog.getCompletionTokens() > 0){
                                            long cost = System.currentTimeMillis() - created;
                                            double costInSeconds = cost / 1000.0;
                                            double l = modelLog.getCompletionTokens() / costInSeconds;
                                            modelLog.setTps((long) Math.floor(l));
                                        }
                                        sseEmitter.send("[DONE]");
                                        sseEmitter.complete();
                                    }
                                } else {
                                    responseJson.put("noStreamConvert", response);

                                    if (Objects.nonNull(modelLog.getCompletionTokens())&& modelLog.getCompletionTokens() > 0){
                                        long cost = System.currentTimeMillis() - created;
                                        double costInSeconds = cost / 1000.0;
                                        double l = modelLog.getCompletionTokens() / costInSeconds;
                                        modelLog.setTps((long) Math.floor(l));
                                    }

                                    sseEmitter.sendBody(JSONObject.toJSONString(response), MediaType.APPLICATION_JSON);
                                    sseEmitter.complete();
                                }
                            }
                        } catch (IOException e) {
                            // 客户端连接异常，关闭连接
                            log.error("Client connection error", e);
                            //记录错误日志
                            modelLog.fillErrorLog(e.getMessage(), Arrays.toString(e.getStackTrace()));
                            extracted(eventConsumeFlag, modelLog, responseJson);
                            sseEmitter.complete();
                        } catch (Exception e) {
                            // 客户端连接异常，关闭连接
                            log.error("Server error", e);
                            modelLog.fillErrorLog(e.getMessage(), Arrays.toString(e.getStackTrace()));
                            sseEmitter.complete();


                            extracted(eventConsumeFlag, modelLog, responseJson);

                        }
                    }
                },
                () -> {
                    extracted(eventConsumeFlag, modelLog, responseJson);
                },
                error->{
                    sseEmitter.complete();
                    extracted(eventConsumeFlag, modelLog, responseJson);
                }
        );
        // 添加错误处理器
        sseEmitter.onError(error -> {
            log.error("SSE emitter error", error);
            disposable.dispose();
        });

        // 添加完成处理器
        sseEmitter.onCompletion(() -> {
            log.debug("SSE emitter completed");
            disposable.dispose();
        });

    }

    private static String getChatCompletionId(ModelRequestCommand command, long created) {
        String id = "chatcmpl-" + UUID.nameUUIDFromBytes((command.getApiKey() + created + UUID.randomUUID()).getBytes());
        return id;
    }

    private void extracted(AtomicBoolean eventConsumeFlag, ModelLog modelLog, JSONObject responseJson) {
        boolean b = !eventConsumeFlag.compareAndExchange(false, true);
        if (b){
            modelLog.setResponseJson(responseJson.toJSONString());
            ModelLogInsertEvent modelLogInsertEvent = new ModelLogInsertEvent(this, modelLog);
            applicationEventPublisher.publishEvent(modelLogInsertEvent);
        }
    }
}
