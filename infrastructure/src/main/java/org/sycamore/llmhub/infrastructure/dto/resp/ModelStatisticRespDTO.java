package org.sycamore.llmhub.infrastructure.dto.resp;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/7/22 16:27
 * @version: 1.0
 * @description: TODO
 */
@Data
public class ModelStatisticRespDTO {
    private String id;
    private Long avgTps;
    private Long avgFirstChunkDelay;
    private Long totalPromptTokens;
    private Long totalCompletionTokens;

    private Long totalTokens;


}
