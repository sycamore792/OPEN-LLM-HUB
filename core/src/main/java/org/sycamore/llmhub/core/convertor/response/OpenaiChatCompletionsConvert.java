package org.sycamore.llmhub.core.convertor.response;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.sycamore.llmhub.core.model.openai.OpenAiChatResponseModel;
import org.sycamore.llmhub.infrastructure.strategy.AbstractExecuteStrategy;

import java.util.List;

import static com.alibaba.fastjson2.JSONReader.Feature.UseNativeObject;
import static org.sycamore.llmhub.core.convertor.response.OutApiResponseStrategyMarkEnum.OPEN_AI_CHAT_COMPLETIONS;

/**
 * @author: Sycamore
 * @date: 2024/7/20 17:26
 * @version: 1.0
 * @description: TODO
 */
@Component
public class OpenaiChatCompletionsConvert extends AbstractChatCompletionsResponseHandler implements AbstractExecuteStrategy<String, OpenAiChatResponseModel>  {
    @Override
    public String mark() {
        return OPEN_AI_CHAT_COMPLETIONS.getMark();
    }

    public OpenAiChatResponseModel executeResp(String event) {
        event = doneHandler(event);
        if ("[DONE]".equals(event)){
            return OpenAiChatResponseModel.stopChoice("1111", 0L, "111", null);
        }
        OpenAiChatResponseModel openAiChatResponseModel = JSON.parseObject(event, OpenAiChatResponseModel.class);

//        if ( openAiChatResponseModel.getChoices() != null && !openAiChatResponseModel.getChoices().isEmpty()) {
//            String finishReason = openAiChatResponseModel.getChoices().get(0).getFinishReason();
//            if ("stop".equals(finishReason)) {
//                return OpenAiChatResponseModel.stopChoice("1111", 0L, "111", null);
//            }
//        }
//

        return openAiChatResponseModel;
    }


    @Override
    public String doneHandler(String event) {
        if (event.contains("[DONE]")){
            return "[DONE]";
        }
        return event;
    }
}
