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
public class ModelWithTypeInfoRespDTO {
    private Long id;
    private String modelType;
    private String modelTypeDesc;
    private int protocolCode;

    private String remark;
}
