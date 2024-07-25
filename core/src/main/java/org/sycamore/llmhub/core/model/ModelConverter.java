package org.sycamore.llmhub.core.model;


import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;
import org.sycamore.llmhub.core.model.groq.GroqOpenAiChatResponseModel;
import org.sycamore.llmhub.core.model.groq.GroqOpenAiChatUsageModel;
import org.sycamore.llmhub.core.model.groq.XGroq;
import org.sycamore.llmhub.core.model.openai.OpenAiChatResponseModel;
import org.sycamore.llmhub.core.model.openai.OpenAiChatUsageModel;

/**
 * @author 桑运昌
 */
@Mapper
public interface ModelConverter {
    ModelConverter INSTANCE = Mappers.getMapper(ModelConverter.class);


    OpenAiChatResponseModel groq2openaiResponse(GroqOpenAiChatResponseModel person);

    OpenAiChatUsageModel groq2openaiUsage(XGroq person);
}
