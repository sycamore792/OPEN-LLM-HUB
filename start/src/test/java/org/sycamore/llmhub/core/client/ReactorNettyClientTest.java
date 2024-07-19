package org.sycamore.llmhub.core.client;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.sycamore.llmhub.start.MainApplication;

import javax.net.ssl.SSLException;
import java.util.Map;

/**
 * @author: Sycamore
 * @date: 2024/7/19 11:41
 * @version: 1.0
 * @description: TODO
 */
@SpringBootTest(classes = MainApplication.class)
@RequiredArgsConstructor
public class ReactorNettyClientTest {

    private final ReactorNettyClient reactorNettyClient;


    @Test
    public void test() throws SSLException {
        reactorNettyClient.sendRequest(
                "https://ark.cn-beijing.volces.com/api/v3/chat/completions",
                """
                        {
                 
                    "messages": [
                        {
                        "role": "user",
                        "content": "你好"
                        }
                    ],
                        "stream" : true,
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
                }
                        """,
                Map.of("Authorization", "Bearer "),
                System.out::println,
                null);
        
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
