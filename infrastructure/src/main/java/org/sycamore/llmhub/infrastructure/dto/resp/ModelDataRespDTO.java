package org.sycamore.llmhub.infrastructure.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * @author: Sycamore
 * @date: 2024/7/23 16:42
 * @version: 1.0
 * @description: TODO
 */

@Data
public class ModelDataRespDTO {
    private List<ApiRequestCountRespDTO> apiRequestCount;
    private List<ApiTokensCostRespDTO> apiTokensCost;
    private  List<ApiModelUsedInfoRespDTO> apiModelUsedInfo;
}
