package org.sycamore.llmhub.core;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.sycamore.llmhub.core.client.ReactorNettyClient;
import org.sycamore.llmhub.core.handler.api.OuterApiChainMarkEnum;
import org.sycamore.llmhub.core.model.openai.OpenAiChatRequestModel;
import org.sycamore.llmhub.core.service.ModelServiceI;
import org.sycamore.llmhub.infrastructure.chain.AbstractChainContext;
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

    private final ModelServiceI modelService;
    private final AbstractChainContext<ModelRequestCommand> abstractChainContext;

    @PostMapping("/v1/chat/completions")
    public SseEmitter chatCompletions(@RequestHeader("Authorization") String authorization, @RequestBody OpenAiChatRequestModel requestModel) {
        ModelRequestCommand command = ModelRequestCommand.builder().requestModel(requestModel).apiKey(authorization).build();
        abstractChainContext.handler(OuterApiChainMarkEnum.OUTER_API_REQUEST_FILTER.name(), command);

        SseEmitter sseEmitter = new SseEmitter(-1L);
        modelService.chatCompletions(command,sseEmitter);

        return sseEmitter;
    }

}
