package org.sycamore.llmhub.infrastructure.dto.resp;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author: Sycamore
 * @date: 2024/7/21 19:57
 * @version: 1.0
 * @description: TODO
 */
@Data
public class ModelWithCompanyInfoRespDTO {
    private Long id;
    private String authorCompanyNameZh;
    private String deployCompanyNameZh;

    private String modelName;
    private String modelServerName;
    private String modelServerBaseUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private ModelHealthyRespDTO modelHealthyInfo;

}
