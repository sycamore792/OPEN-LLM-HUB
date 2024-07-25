package org.sycamore.llmhub.core.model.groq;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/7/25 18:14
 * @version: 1.0
 * @description: TODO
 */
@Data
public class XGroq {
    private String id;
    private GroqOpenAiChatUsageModel usage;
}
