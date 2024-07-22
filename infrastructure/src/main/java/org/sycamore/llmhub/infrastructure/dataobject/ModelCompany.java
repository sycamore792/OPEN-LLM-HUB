package org.sycamore.llmhub.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sycamore.llmhub.infrastructure.common.BaseDO;

import java.util.Date;

/**
 * @author: Sycamore
 * @date: 2024/7/21 2:11
 * @version: 1.0
 * @description: TODO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("model_company")
public class ModelCompany extends BaseDO {
    private String id;
    private String companyNameEn;
    private String companyNameZh;
    private String companyIcon;
}

