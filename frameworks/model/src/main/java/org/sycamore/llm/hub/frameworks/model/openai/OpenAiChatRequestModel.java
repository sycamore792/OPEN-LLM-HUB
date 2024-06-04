package org.sycamore.llm.hub.frameworks.model.openai;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
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
    @JSONField(name = "max_tokens")
    private Long maxTokens;
    private Double temperature;
    @JSONField(name = "top_p")
    private Double topP;
    /**
     * <p>介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其是否已在已有文本中出现受到相应的惩罚，从而增加模型谈论新主题的可能性。</p>
     */
    @JSONField(name = "presence_penalty")
    private Double presencePenalty;
    /**
     * <p>介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其在已有文本中的出现频率受到相应的惩罚，降低模型重复相同内容的可能性。</p>
     */
    @JSONField(name = "frequency_penalty")
    private Double frequencyPenalty;
    private List<String> stop;
    private Integer n;
    /**
     * <p>如果设置为 True，将会以 SSE（server-sent events）的形式以流式发送消息增量。消息流以 data: [DONE] 结尾。</p>
     */
    private Boolean stream;

    private String user;

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
        if (Objects.isNull(stream)){
            this.stream = false;
        }

        return  true;
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
