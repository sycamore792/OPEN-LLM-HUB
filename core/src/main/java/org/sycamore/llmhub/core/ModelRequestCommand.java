package org.sycamore.llmhub.core;

import lombok.Builder;
import lombok.Data;
import org.sycamore.llmhub.core.model.openai.OpenAiChatRequestModel;
import org.sycamore.llmhub.infrastructure.dto.resp.SelectModelServerInfoByKeyRespDTO;

/**
 * @author: Sycamore
 * @date: 2024/7/19 14:38
 * @version: 1.0
 * @description: TODO
 */

@Data
@Builder
public class ModelRequestCommand {
    private OpenAiChatRequestModel requestModel;
    private String apiKey;
    private SelectModelServerInfoByKeyRespDTO modelServerInfo;
}
