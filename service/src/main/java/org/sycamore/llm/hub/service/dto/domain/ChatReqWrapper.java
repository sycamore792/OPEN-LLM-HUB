package org.sycamore.llm.hub.service.dto.domain;

import lombok.Data;
import org.sycamore.llm.hub.service.dto.req.ChatReqDTO;

/**
 * @author: Sycamore
 * @date: 2024/4/16 18:00
 * @version: 1.0
 * @description: TODO
 */
@Data
public class ChatReqWrapper {
    private ChatReqDTO chatReqDTO;
    private String key;
}
