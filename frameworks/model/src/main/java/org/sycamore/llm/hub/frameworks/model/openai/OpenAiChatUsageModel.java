package org.sycamore.llm.hub.frameworks.model.openai;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:21
 * @version: 1.0
 * @description: TODO
 */
@Data
public class OpenAiChatUsageModel {

    private Integer promptTokens;
    private Integer totalTokens;
    private Integer completionTokens;

}
