package org.sycamore.llmhub.core.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.sycamore.llmhub.core.ModelRequestCommand;
import org.sycamore.llmhub.core.client.ReactorNettyClient;
import org.sycamore.llmhub.core.service.ModelServiceI;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelMapper;
import org.sycamore.llmhub.infrastructure.dto.resp.SelectModelServerInfoByKeyRespDTO;
import reactor.core.Disposable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
    private final ModelMapper modelMapper;

    @Override
    public void chatCompletions(ModelRequestCommand command, SseEmitter sseEmitter) {
        String apiKey = command.getApiKey();
        String model = command.getRequestModel().getModel();
        // 通过 apiKey 与 model 加载配置
        SelectModelServerInfoByKeyRespDTO modelServerInfoRespDTO = modelMapper.selectModelServerInfoByKey(apiKey, model).get(0);

        // load 请求配置 （缓存）
        String url = modelServerInfoRespDTO.getModelServerBaseUrl() +"/chat/completions";
        JSONObject reqParamsJson = JSONObject.parseObject(modelServerInfoRespDTO.getServerParams());
        Map<String, String> headerMap = new HashMap<>();
        JSONArray headers = reqParamsJson.getJSONArray("headers");
        headers.forEach(header -> {
            JSONObject headerJson = JSONObject.from(header);
            headerMap.put(headerJson.getString("key"), headerJson.getString("value"));
        });
        headerMap.put("Content-Type", "application/json");
        String modelServerName = modelServerInfoRespDTO.getModelServerName();

/**
 * ,
 *                                 "stream_options":{
 *                                 "include_usage":true
 *                                 },
 *                                 "tools": [
 *                                 {
 *                                 "type": "function",
 *                                 "function": {
 *                                     "name": "get_current_weather",
 *                                     "description": "Get the current weather in a given location",
 *                                     "parameters": {
 *                                         "type": "object",
 *                                         "properties": {
 *                                             "location": {
 *                                             "type": "string",
 *                                             "description": "The city and state, e.g. San Francisco, CA"
 *                                             },
 *                                             "unit": {
 *                                                 "type": "string",
 *                                                 "enum": ["celsius", "fahrenheit"]
 *                                             }
 *                                         },
 *                                         "required": ["location"]
 *                                     }
 *                                 }
 *                             }
 *                             ]
 */
        // 请求体适配
        String body = String.format("""
                        {
                            "model": "%s",
                            "messages": [
                                {
                                "role": "user",
                                "content": "你谁啊"
                                }
                            ],
                            "stream" : true,
                            "stream_options":{
                            "include_usage":true
                             }
                        }""",
                modelServerName
        );
        // 加载响应体适配策略

        // 调用模型服务
        Disposable disposable = reactorNettyClient.sendRequest(
                url,
                body,
                headerMap,
                event -> {
                    try {
                        sseEmitter.send(event);
                    } catch (IOException e) {
                        // 客户端连接异常，关闭连接
                        log.error("Client connection error", e);
                        sseEmitter.completeWithError(e);
                    }
                },
                sseEmitter::complete);
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
