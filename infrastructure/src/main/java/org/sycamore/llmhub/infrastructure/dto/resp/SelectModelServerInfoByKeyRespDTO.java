package org.sycamore.llmhub.infrastructure.dto.resp;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/5/8 15:26
 * @version: 1.0
 * @description: TODO
 */
@Data
public class SelectModelServerInfoByKeyRespDTO {
    private Long llmModelId;
    private String apiKey;
    private String modelServerName;
    private String modelServerBaseUrl;
    private String serverParams;
    private Integer protocolCode;
}
