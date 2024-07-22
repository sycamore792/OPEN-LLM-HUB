package org.sycamore.llmhub.infrastructure.dto.resp;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author: Sycamore
 * @date: 2024/7/21 22:44
 * @version: 1.0
 * @description: TODO
 */

@Data
public class ModelInfoRespDTO {
    private String id;

    private String authorCompanyNameZh;
    private String deployCompanyNameZh;
    private String modelName;
    private String modelServerBaseUrl;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    private String modelServerName;
    private String modelType;
    private String modelTypeCode;
    private int protocolCode;
    private String protocolDesc;
    private String remark;

    private ModelStatisticRespDTO modelStatistic;
}
