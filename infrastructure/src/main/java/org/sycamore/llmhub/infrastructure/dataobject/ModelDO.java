package org.sycamore.llmhub.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sycamore.llmhub.infrastructure.common.BaseDO;

/**
 * @author: Sycamore
 * @date: 2024/5/8 15:22
 * @version: 1.0
 * @description: TODO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_llm")
public class ModelDO extends BaseDO {
    private Long id;
    private Long authorCompanyId;
    private Long deployCompanyId;
    private String modelName;
    private String modelServerName;
    private String modelServerBaseUrl;
}
