package org.sycamore.llm.hub.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.sycamore.llm.hub.service.dao.BaseDO;

/**
 * @author: Sycamore
 * @date: 2024/4/26 13:50
 * @version: 1.0
 * @description: TODO
 */
@Data
@TableName("api_key")
public class ApiKeyDO extends BaseDO {
    private Long id;
    private Long userId;
    private String apiKey;
}
