package org.sycamore.llmhub.infrastructure.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 桑运昌
 */


@Getter
@AllArgsConstructor
public enum OutApiResponseStrategyMarkEnum {
    OPEN_AI_CHAT_COMPLETIONS(1, "openai-chat-completions"),
    GROQ_OPEN_AI_CHAT_COMPLETIONS(2, "groq-openai-chat-completions");
    private final Integer code;
    private final String mark;

    public static OutApiResponseStrategyMarkEnum getByCode(Integer code) {
        for (OutApiResponseStrategyMarkEnum value : OutApiResponseStrategyMarkEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
