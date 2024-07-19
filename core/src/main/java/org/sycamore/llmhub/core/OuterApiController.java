package org.sycamore.llmhub.core;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.sycamore.llmhub.core.client.ReactorNettyClient;
import reactor.core.Disposable;

import java.io.IOException;
import java.util.Map;

/**
 * @author: Sycamore
 * @date: 2024/7/19 11:37
 * @version: 1.0
 * @description: TODO
 */
@RestController
@RequiredArgsConstructor
public class OuterApiController {

    private final ReactorNettyClient reactorNettyClient;

    @GetMapping("/api/v1/chat")
    public SseEmitter chat() {
        SseEmitter sseEmitter = new SseEmitter(-1L);
        Disposable disposable = reactorNettyClient.sendRequest(
                "https://ark.cn-beijing.volces.com/api/v3/chat/completions",
                """
                                {
                            "model": "",
                            "messages": [
                                {
                                "role": "user",
                                "content": "你好"
                                }
                            ],
                                "stream" : true,
                                "stream_options":{
                                "include_usage":true
                                },
                                "tools": [
                                {
                                "type": "function",
                                "function": {
                                    "name": "get_current_weather",
                                    "description": "Get the current weather in a given location",
                                    "parameters": {
                                        "type": "object",
                                        "properties": {
                                            "location": {
                                            "type": "string",
                                            "description": "The city and state, e.g. San Francisco, CA"
                                            },
                                            "unit": {
                                                "type": "string",
                                                "enum": ["celsius", "fahrenheit"]
                                            }
                                        },
                                        "required": ["location"]
                                    }
                                }
                            }
                            ]
                        }""",
                Map.of("Authorization", "Bearer "),
                event -> {
                    try {
                        sseEmitter.send(event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                sseEmitter::complete);
        return sseEmitter;
    }

}
