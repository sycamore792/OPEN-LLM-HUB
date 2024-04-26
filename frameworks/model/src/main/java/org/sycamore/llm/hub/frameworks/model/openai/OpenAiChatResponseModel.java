package org.sycamore.llm.hub.frameworks.model.openai;

import lombok.Data;

import java.util.List;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:20
 * @version: 1.0
 * @description: TODO
 */
@Data
public class OpenAiChatResponseModel {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<OpenAiChatChoiceModel> choices;
    private OpenAiChatUsageModel usage;
}


//public static void main(String[] args){
//    User user = new User();
//    user.setNameInfo("coder");
//    user.setAgeInfo("28");
//
//    //序列化配置，定义将实体中的驼峰命名转化为下划线
//    SerializeConfig config = new SerializeConfig();
//    config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
//
//    String json = JSON.toJSONString(user, config);
//    System.out.println(json);
//}