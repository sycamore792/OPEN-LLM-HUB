package org.sycamore.llm.hub.frameworks.model.openai;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:21
 * @version: 1.0
 * @description: TODO
 */
@Data
public class OpenAiChatUsageModel {
    @JSONField(name = "prompt_tokens")
    private Integer promptTokens;
    @JSONField(name = "total_tokens")
    private Integer totalTokens;
    @JSONField(name = "completion_tokens")
    private Integer completionTokens;

}
