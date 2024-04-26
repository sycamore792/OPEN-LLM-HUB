package org.sycamore.llm.hub.service.netty.adapter.req;

import org.sycamore.llm.hub.frameworks.model.openai.OpenAiChatRequestModel;
import org.sycamore.llm.hub.service.dto.domain.ConvertedResponseDTO;
import org.sycamore.llm.hub.service.netty.adapter.IRequestConvertAdapter;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:30
 * @version: 1.0
 * @description: TODO
 */

public class OpenAiRequestConvertAdapter implements IRequestConvertAdapter {
    @Override
    public boolean support(String contentType) {
        return false;
    }

    @Override
    public Object convert(OpenAiChatRequestModel response) {
        return response;
    }
}
