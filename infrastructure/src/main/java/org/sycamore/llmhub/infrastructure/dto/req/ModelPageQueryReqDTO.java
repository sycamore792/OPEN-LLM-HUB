package org.sycamore.llmhub.infrastructure.dto.req;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/7/22 10:33
 * @version: 1.0
 * @description: TODO
 */

@Data
public class ModelPageQueryReqDTO {
    private String modelName;
    private String deployCompanyId;
    private String authorCompanyId;
}
