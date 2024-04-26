package org.sycamore.llm.hub.frameworks.model.openai;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.sycamore.llm.hub.frameworks.model.openai.OpenAiMessageModel.*;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:16
 * @version: 1.0
 * @description: TODO
 */
@Data
public class OpenAiChatRequestModel {
    private String model;
    private List<OpenAiMessageModel> messages;
    private Long maxTokens;
    private Double temperature;
    private Double topP;
    private Double presencePenalty;
    private Double frequencyPenalty;
    private List<String> stop;
    private Integer n;

    private Boolean stream;
    public Boolean isValid(){
        if (Objects.isNull(messages) || messages.isEmpty()){
            return false;
        }
        if (isBlank(messages.get(0).getRole()) || isBlank(messages.get(0).getContent())){
            return false;
        }
        List<OpenAiMessageModel> history;
        if (messages.get(0).getRole().equals(ROLE_SYSTEM)){
            history = messages.subList(1, messages.size());
        }else {
            history = messages.subList(0, messages.size());
        }
        String currentRole = history.get(0).getRole();
        HashMap<Integer, String> roleMap = new HashMap<>();
        roleMap.put(1, currentRole);
        roleMap.put(-1,currentRole.equals(ROLE_USER)?ROLE_ASSISTANT:ROLE_USER);
        int current = 1;
        for (OpenAiMessageModel message : history){
            if (Objects.isNull(message.getRole()) || Objects.isNull(message.getContent())){
                return false;
            }
            if (!message.getRole().equals(roleMap.get(current))){
                return false;
            }
            current = current * -1;
        }

        return Objects.nonNull(stream)
                && Objects.nonNull(maxTokens);
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs != null) {
            int length = cs.length();
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
