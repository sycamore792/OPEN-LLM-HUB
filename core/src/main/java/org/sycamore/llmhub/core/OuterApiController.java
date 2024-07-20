package org.sycamore.llmhub.core;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.sycamore.llmhub.core.handler.api.OuterApiChainMarkEnum;
import org.sycamore.llmhub.core.model.openai.OpenAiChatRequestModel;
import org.sycamore.llmhub.core.service.ModelServiceI;
import org.sycamore.llmhub.infrastructure.chain.AbstractChainContext;
import org.sycamore.llmhub.infrastructure.common.SseEmitter;


/**
 * @author: Sycamore
 * @date: 2024/7/19 11:37
 * @version: 1.0
 * @description: TODO
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "外部开放接口")
public class OuterApiController {

    private final ModelServiceI modelService;
    private final AbstractChainContext<ModelRequestCommand> abstractChainContext;

    @PostMapping("/v1/chat/completions")
    @Operation(summary = "chatCompletions")
    public SseEmitter chatCompletions(@RequestHeader("Authorization") String authorization, @RequestBody OpenAiChatRequestModel requestModel) {
        ModelRequestCommand command = ModelRequestCommand.builder().requestModel(requestModel).apiKey(authorization).build();
        abstractChainContext.handler(OuterApiChainMarkEnum.OUTER_API_REQUEST_FILTER.name(), command);
        SseEmitter sseEmitter = new SseEmitter(-1L);
        modelService.chatCompletions(command,sseEmitter);
        return sseEmitter;
    }

    @GetMapping("/v1/models")
    @Operation(summary = "查看可用的模型列表")
    public Object getModels(@RequestHeader("Authorization") String authorization) {

        return null;
    }

}
