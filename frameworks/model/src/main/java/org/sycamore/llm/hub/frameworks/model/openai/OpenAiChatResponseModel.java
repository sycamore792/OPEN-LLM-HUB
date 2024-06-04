package org.sycamore.llm.hub.frameworks.model.openai;

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
    private String id;
    private String object;
    private Long created;
    private String model;
    @JSONField(name = "system_fingerprint")
    private String systemFingerPrint;
    private List<OpenAiChatChoiceModel> choices;
    private OpenAiChatUsageModel usage;
}