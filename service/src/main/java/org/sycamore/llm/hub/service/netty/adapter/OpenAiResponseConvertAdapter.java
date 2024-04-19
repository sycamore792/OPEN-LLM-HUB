package org.sycamore.llm.hub.service.netty.adapter;

import org.springframework.stereotype.Component;
import org.sycamore.llm.hub.service.dto.domain.ConvertedResponseDTO;
/**
 * @author: Sycamore
 * @date: 2024/4/19 23:56
 * @version: 1.0
 * @description: TODO
 */
@Component
public class OpenAiResponseConvertAdapter implements IResponseConvertAdapter{
    private static final String END_OF_RESPONSE = "[DONE]";

    @Override
    public boolean support(String contentType) {
        return false;
    }

    @Override
    public ConvertedResponseDTO convert(String response) {
        ConvertedResponseDTO convertedResponseDTO = new ConvertedResponseDTO();
        if (response.equals(END_OF_RESPONSE)){
            convertedResponseDTO.setStopFlag(true);
            return convertedResponseDTO;
        }

        return convertedResponseDTO;
    }
}
