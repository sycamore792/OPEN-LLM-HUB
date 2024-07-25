package org.sycamore.llmhub.core.model.groq;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import org.sycamore.llmhub.core.model.Covert2OpenAiI;
import org.sycamore.llmhub.core.model.ModelConverter;
import org.sycamore.llmhub.core.model.openai.OpenAiChatChoiceModel;
import org.sycamore.llmhub.core.model.openai.OpenAiChatResponseModel;
import org.sycamore.llmhub.core.model.openai.OpenAiChatUsageModel;
import org.sycamore.llmhub.core.model.openai.OpenAiMessageModel;
import org.sycamore.llmhub.infrastructure.common.BeanUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:20
 * @version: 1.0
 * @description: TODO
 */
@Data
public class GroqOpenAiChatResponseModel implements Covert2OpenAiI {
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
    private GroqOpenAiChatUsageModel usage;


    @JSONField(serialize = false)
    private boolean doneFlag;


    @JSONField(name = "x_groq")
    private XGroq xGroq;

    public static GroqOpenAiChatResponseModel stopChoice(String id, Long created, String model, GroqOpenAiChatUsageModel usage) {
        GroqOpenAiChatResponseModel response = new GroqOpenAiChatResponseModel();
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

    @Override
    public OpenAiChatResponseModel convert() {
        OpenAiChatResponseModel openAiChatResponseModel = ModelConverter.INSTANCE.groq2openaiResponse(this);

        if (Objects.nonNull(this.xGroq)&& Objects.nonNull(this.xGroq.getUsage())){
            openAiChatResponseModel.setUsage(ModelConverter.INSTANCE.groq2openaiUsage(this.xGroq.getUsage()));
        }else {
            openAiChatResponseModel.setUsage(null);
        }
        return openAiChatResponseModel;
    }
}