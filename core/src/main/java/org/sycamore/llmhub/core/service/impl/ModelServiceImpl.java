package org.sycamore.llmhub.core.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import org.sycamore.llmhub.core.ModelRequestCommand;
import org.sycamore.llmhub.core.client.ReactorNettyClient;
import org.sycamore.llmhub.core.convertor.response.OutApiResponseStrategyMarkEnum;
import org.sycamore.llmhub.core.model.openai.OpenAiChatRequestModel;
import org.sycamore.llmhub.core.model.openai.OpenAiChatResponseModel;
import org.sycamore.llmhub.core.model.openai.OpenAiStreamOptionModel;
import org.sycamore.llmhub.core.service.ModelServiceI;
import org.sycamore.llmhub.infrastructure.common.SseEmitter;
import org.sycamore.llmhub.infrastructure.dto.resp.SelectModelServerInfoByKeyRespDTO;
import org.sycamore.llmhub.infrastructure.strategy.AbstractStrategyChoose;
import reactor.core.Disposable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        requestModel.setModel(modelServerInfoRespDTO.getModelServerName());
        if (requestModel.isStream()){
            requestModel.setStreamOption(new OpenAiStreamOptionModel());
        }


        // todo 根据协议code加载convert策略 目前默认为openai（模型确实也都是接的openai协议的）所以直接字符串化
        String body = JSONObject.toJSONString(requestModel);

        //响应体公共参数值
        String id = "id000000000";
        long created = System.currentTimeMillis();
        String model = requestModel.getModel();


        // 调用模型服务
        Disposable disposable = reactorNettyClient.sendRequest(
                requestModel.isStream(),
                url,
                body,
                headerMap,
                event -> {
                    if (!StringUtils.isBlank(event)) {
                        try {
                            log.info("event:{}", event);

                            // 加载响应体适配策略
                            String mark = Objects.requireNonNull(OutApiResponseStrategyMarkEnum.getByCode(protocolCode)).getMark();
                            OpenAiChatResponseModel response = abstractStrategyChoose.chooseAndExecuteResp(mark, event);
                            if (Objects.nonNull(response)) {
                                //公共参数填充
                                response.setModel(model);
                                response.setId(id);
                                response.setCreated(created);

                                if (requestModel.isStream()){
                                    sseEmitter.send(JSONObject.toJSONString(response));
                                    log.info("response:{}", response);

                                    // 检查 response 和 choices 是否为空，choices 的长度是否大于 0
                                    if (response.isDoneFlag()) {
                                        sseEmitter.send("[DONE]");
                                        sseEmitter.complete();
                                    }
                                }else {
                                    sseEmitter.sendBody(JSONObject.toJSONString(response), MediaType.APPLICATION_JSON);
                                    sseEmitter.complete();
                                }
                            }

                        } catch (Exception e) {
                            // 客户端连接异常，关闭连接
                            log.error("Client connection error", e);
                            sseEmitter.complete();
                        }
                    }

                },
                () -> {
                    // todo 发布完成事件
                }
        );
        // 添加错误处理器
        sseEmitter.onError(error -> {
            log.error("SSE emitter error", error);
            disposable.dispose();
        });

        // 添加完成处理器
        sseEmitter.onCompletion(() -> {
            log.info("SSE emitter completed");
            disposable.dispose();
        });

    }
}
