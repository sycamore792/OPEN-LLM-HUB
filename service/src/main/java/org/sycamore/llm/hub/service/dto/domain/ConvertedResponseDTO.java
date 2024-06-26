package org.sycamore.llm.hub.service.dto.domain;

import lombok.Data;
import org.sycamore.llm.hub.frameworks.model.openai.OpenAiChatResponseModel;

/**
 * @author: Sycamore
 * @date: 2024/4/19 23:55
 * @version: 1.0
 * @description: TODO
 */
@Data
public class ConvertedResponseDTO {
    private boolean stopFlag;

    private OpenAiChatResponseModel responseModel;
}
