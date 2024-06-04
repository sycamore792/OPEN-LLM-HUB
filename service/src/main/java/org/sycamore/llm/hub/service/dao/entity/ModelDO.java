package org.sycamore.llm.hub.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.sycamore.llm.hub.service.dao.BaseDO;

/**
 * @author: Sycamore
 * @date: 2024/5/8 15:22
 * @version: 1.0
 * @description: TODO
 */
@Data
@TableName("model_llm")
public class ModelDO extends BaseDO {
    private Long id;
    private Long authorCompanyId;
    private Long deployCompanyId;
    private String modelName;
    private String modelServerName;
    private String modelServerBaseUrl;
}
