package org.sycamore.llm.hub.frameworks.model.openai;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:21
 * @version: 1.0
 * @description: TODO
 */
@Data
public class OpenAiChatChoiceModel {
    private String finishReason;
    private Integer index;
    private OpenAiMessageModel message;
    private OpenAiMessageModel delta;
}
