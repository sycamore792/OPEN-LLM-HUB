package org.sycamore.llmhub.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sycamore.llmhub.infrastructure.common.BaseDO;

/**
 * @author: Sycamore
 * @date: 2024/4/26 13:50
 * @version: 1.0
 * @description: TODO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("api_key")
public class ApiKeyDO extends BaseDO {
    private Long id;
    private Long userId;
    private String apiKey;
}
