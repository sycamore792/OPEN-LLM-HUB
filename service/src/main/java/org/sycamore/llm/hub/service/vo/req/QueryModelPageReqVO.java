package org.sycamore.llm.hub.service.vo.req;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/5/10 17:33
 * @version: 1.0
 * @description: TODO
 */
@Data
public class QueryModelPageReqVO {
    private String modelName;
    private String authorCompanyId;
    private String deployCompanyId;
}
