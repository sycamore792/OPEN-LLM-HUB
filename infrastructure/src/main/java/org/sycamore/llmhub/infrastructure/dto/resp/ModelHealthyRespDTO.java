package org.sycamore.llmhub.infrastructure.dto.resp;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author: Sycamore
 * @date: 2024/7/22 15:32
 * @version: 1.0
 * @description: TODO
 */

@Data
public class ModelHealthyRespDTO {
    private int healthy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime update;

}
