package org.sycamore.llm.hub.service.dto.req;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/5/10 17:38
 * @version: 1.0
 * @description: TODO
 */
@Data
public class QueryModelPageReqDTO {
    private String modelName;
    private Long authorCompanyId;
    private Long deployCompanyId;
}
