package org.sycamore.llmhub.core.model.openai;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;
import java.util.Optional;

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
    private String object = "chat.completion";
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


    @JSONField(serialize = false)
    private boolean doneFlag;

    public static OpenAiChatResponseModel stopChoice(String id,Long created,String model,OpenAiChatUsageModel usage) {
        OpenAiChatResponseModel response = new OpenAiChatResponseModel();
        OpenAiChatChoiceModel openAiChatChoiceModel = new OpenAiChatChoiceModel();
        openAiChatChoiceModel.setMessage(new OpenAiMessageModel());
        openAiChatChoiceModel.setIndex(0);
        openAiChatChoiceModel.setFinishReason("stop");



        response.setChoices(List.of( openAiChatChoiceModel));
        response.setId(id);
        response.setCreated(created);
        response.setModel(model);
        response.setDoneFlag(true);
        Optional.ofNullable(usage).ifPresent(response::setUsage);
        return response;
    }
}