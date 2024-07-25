package org.sycamore.llmhub.core.model.groq;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:21
 * @version: 1.0
 * @description:
 *      {
 *      "queue_time":0.023833446999999952,
 *      "prompt_tokens":992,
 *      "prompt_time":0.252965635,
 *      "completion_tokens":368,
 *      "completion_time":1.472,
 *      "total_tokens":1360,
 *      "total_time":1.724965635
 *      }
 */
@Data
public class GroqOpenAiChatUsageModel {
    @JSONField(name = "queue_time")
    private Float queueTime;

    @JSONField(name = "prompt_tokens")
    private Integer promptTokens;
    @JSONField(name = "prompt_time")
    private Float promptTime;
    @JSONField(name = "total_tokens")
    private Integer totalTokens;
    @JSONField(name = "total_time")
    private Float totalTime;


    @JSONField(name = "completion_tokens")
    private Integer completionTokens;
    @JSONField(name = "completion_time")
    private Float completionTime;

}
