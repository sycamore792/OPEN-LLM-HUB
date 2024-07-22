package org.sycamore.llmhub.core.model.openai;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:09
 * @version: 1.0
 * @description: TODO
 */
@Data
public class OpenAiMessageModel {
    public static  final String ROLE_USER = "user";
    public static  final String ROLE_SYSTEM = "system";
    public static  final String ROLE_ASSISTANT = "assistant";

    private String role;
    private String content;
    @JSONField(name = "tool_calls")
    private List<ToolCall> toolCalls;
    @Data
    class ToolCall {
        private String id;

        private String type;
        private ToolCallFunction function;
    }

    @Data
    class ToolCallFunction {
        private String name;
        private String arguments;
    }

    public static OpenAiMessageModel of(String role, String content) {
        OpenAiMessageModel message = new OpenAiMessageModel();
        message.setRole(role);
        message.setContent(content);
        return message;
    }

    public static OpenAiMessageModel ofUser(String content) {
        return of(ROLE_USER, content);
    }

    public static OpenAiMessageModel ofSystem(String content) {
        return of(ROLE_SYSTEM, content);
    }

    public static OpenAiMessageModel ofAssistant(String content) {
        return of(ROLE_ASSISTANT, content);
    }

    /**
     * @description: 传入多个消息对象，返回数组
     * @param
     * @return
     */
    public static List<OpenAiMessageModel> list(OpenAiMessageModel... messages) {
      return   Arrays.asList(messages);
    }
}
