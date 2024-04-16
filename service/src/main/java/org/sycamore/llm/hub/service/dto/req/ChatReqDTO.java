package org.sycamore.llm.hub.service.dto.req;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/4/16 17:42
 * @version: 1.0
 * @description: TODO
 */
@Data
public class ChatReqDTO {
    private String model;
    /**
     * 0-2.0
     */
    private Double temperature;

    /**
     * 0-1
     */
    private Double topP;


    public Boolean isValid(){
        return true;
    }
}
