package org.sycamore.llmhub.core.model.openai;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:20
 * @version: 1.0
 * @description: TODO
 */
@Data
public class OpenAiChatResponseModel {
    /**
     * 一次 chat completion 接口调用的唯一标识。
     */
    private String id;
    /**
     * 固定为 chat.completion。
     */
    private String object;
    /**
     * 本次对话生成时间戳（秒）。
     */
    private Long created;
    /**
     * 实际使用的模型名称和版本。
     */
    private String model;
    @JSONField(name = "system_fingerprint")
    private String systemFingerPrint;
    private List<OpenAiChatChoiceModel> choices;
    private OpenAiChatUsageModel usage;
}