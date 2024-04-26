package org.sycamore.llm.hub.frameworks.model.openai;

import lombok.Data;

import java.util.List;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:16
 * @version: 1.0
 * @description: TODO
 */
@Data
public class OpenAiChatRequestModel {
    private String model;
    private List<OpenAiMessageModel> messages;
    private Long maxTokens;
    private Double temperature;
    private Double topP;
    private Double presencePenalty;
    private Double frequencyPenalty;
    private List<String> stop;
    private Integer n;

    private Boolean stream;

}
