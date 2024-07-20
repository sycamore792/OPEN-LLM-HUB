package org.sycamore.llmhub.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: Sycamore
 * @date: 2024/7/21 2:11
 * @version: 1.0
 * @description: TODO
 */
@Data
@TableName("model_log")
public class ModelLog {
    private String id;
    private Long modelId;
    private String apiKey;
    private String requestJson;
    private String responseJson;
    private Long created;
    private Long promptTokens;
    private Long completionTokens;
    private Long totalTokens;


    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}

