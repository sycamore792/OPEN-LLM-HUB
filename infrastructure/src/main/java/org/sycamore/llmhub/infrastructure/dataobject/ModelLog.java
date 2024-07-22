package org.sycamore.llmhub.infrastructure.dataobject;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
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
    private String errorJson;
    private Long created;
    private Long promptTokens;
    private Long completionTokens;
    private Long totalTokens;
    private Long tps;
    private Long firstChunkDelay;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    public void fillErrorLog(String errorMsg, String errorStack) {
        ModelErrorLog modelErrorLog = new ModelErrorLog(errorMsg, errorStack);
        this.setErrorJson(JSON.toJSONString(modelErrorLog));
    }
}
