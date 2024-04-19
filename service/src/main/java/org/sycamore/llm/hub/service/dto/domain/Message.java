package org.sycamore.llm.hub.service.dto.domain;

import lombok.Data;

/**
 * @author: Sycamore
 * @date: 2024/4/17 15:32
 * @version: 1.0
 * @description: TODO
 */
@Data
public class Message {
    public static  final String ROLE_USER = "user";
    public static  final String ROLE_SYSTEM = "system";
    public static  final String ROLE_ASSISTANT = "assistant";

    private String role;
    private String content;

    public static Message of(String role, String content) {
        Message message = new Message();
        message.setRole(role);
        message.setContent(content);
        return message;
    }

    public static Message ofUser(String content) {
        return of(ROLE_USER, content);
    }

    public static Message ofSystem(String content) {
        return of(ROLE_SYSTEM, content);
    }

    public static Message ofAssistant(String content) {
        return of(ROLE_ASSISTANT, content);
    }
}
