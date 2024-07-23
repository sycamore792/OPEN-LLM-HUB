package org.sycamore.llmhub.infrastructure.dto.resp;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/7/23 16:42
 * @version: 1.0
 * @description: TODO
 */

@Data
public class ApiModelUsedInfoRespDTO {
    private String id;
    private String modelName;
    private Integer count;
    private float percentage;
}
