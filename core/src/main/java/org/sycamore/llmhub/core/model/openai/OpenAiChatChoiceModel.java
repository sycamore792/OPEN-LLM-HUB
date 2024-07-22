package org.sycamore.llmhub.core.model.openai;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:21
 * @version: 1.0
 * @description: TODO
 */
@Data
public class OpenAiChatChoiceModel {


    private String logprobs;

    private Integer index;
    @JSONField(name = "finish_reason")
    private String finishReason;
    private OpenAiMessageModel message;
    private OpenAiMessageModel delta;
    private OpenAiChatUsageModel usage;
}
