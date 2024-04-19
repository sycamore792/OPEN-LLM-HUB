package org.sycamore.llm.hub.service.dto.req;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;
import org.sycamore.llm.hub.service.dto.domain.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.sycamore.llm.hub.service.dto.domain.Message.*;

/**
 * @author: Sycamore
 * @date: 2024/4/16 17:42
 * @version: 1.0
 * @description: TODO
 */
@Data
public class ChatReqDTO {
    private String user;

    private List<Message> messages;

    private String model;
    /**
     * 0-2.0
     */
    private Double temperature;

    /**
     * 0-1
     */
    @JSONField(name="top_p")
    private Double topP;

    @JSONField(name="max_tokens")
    private Integer maxTokens;

    private Integer n;
    private Boolean stream;
    @JSONField(name="frequency_penalty")
    private Double presencePenalty;
    @JSONField(name="presence_penalty")
    private Double frequencyPenalty;

    public Boolean isValid(){
        if (Objects.isNull(messages) || messages.isEmpty()){
            return false;
        }
        if (StringUtils.isBlank(messages.get(0).getRole()) || StringUtils.isBlank(messages.get(0).getContent())){
            return false;
        }
        List<Message> history;
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
        for (Message message : history){
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



}
