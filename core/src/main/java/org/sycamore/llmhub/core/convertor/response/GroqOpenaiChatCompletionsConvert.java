package org.sycamore.llmhub.core.convertor.response;

import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Component;
import org.sycamore.llmhub.core.model.groq.GroqOpenAiChatResponseModel;
import org.sycamore.llmhub.core.model.openai.OpenAiChatResponseModel;
import org.sycamore.llmhub.infrastructure.strategy.AbstractExecuteStrategy;

import static org.sycamore.llmhub.infrastructure.common.OutApiResponseStrategyMarkEnum.GROQ_OPEN_AI_CHAT_COMPLETIONS;
import static org.sycamore.llmhub.infrastructure.common.OutApiResponseStrategyMarkEnum.OPEN_AI_CHAT_COMPLETIONS;

/**
 * @author: Sycamore
 * @date: 2024/7/20 17:26
 * @version: 1.0
 * @description: TODO
 */
@Component
public class GroqOpenaiChatCompletionsConvert extends AbstractChatCompletionsResponseHandler implements AbstractExecuteStrategy<String, OpenAiChatResponseModel>  {
    @Override
    public String mark() {
        return GROQ_OPEN_AI_CHAT_COMPLETIONS.getMark();
    }

    public OpenAiChatResponseModel executeResp(String event) {
        event = doneHandler(event);
        if ("[DONE]".equals(event)){
            return OpenAiChatResponseModel.stopChoice("", 0L, "", null);
        }
        GroqOpenAiChatResponseModel groqOpenAiChatResponseModel = JSON.parseObject(event, GroqOpenAiChatResponseModel.class);
        return groqOpenAiChatResponseModel.convert();
    }


    @Override
    public String doneHandler(String event) {
        if (event.contains("[DONE]")){
            return "[DONE]";
        }
        return event;
    }
}
