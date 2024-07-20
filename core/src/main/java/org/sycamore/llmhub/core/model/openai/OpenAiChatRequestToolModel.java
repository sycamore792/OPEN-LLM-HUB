package org.sycamore.llmhub.core.model.openai;

import lombok.Data;
import java.util.List;

/**
 * @author: Sycamore
 * @date: 2024/4/26 12:16
 * @version: 1.0
 * @description:
 *
 *
 * "tools": [
 *            {
 *                               "type": "function",
 *                               "function": {
 *                                   "name": "get_current_weather",
 *                                   "description": "Get the current weather in a given location",
 *                                   "parameters": {
 *                                       "type": "object",
 *                                       "properties": {
 *                                           "location": {
 *                                           "type": "string",
 *                                           "description": "The city and state, e.g. San Francisco, CA"
 *                                           },
 *                                           "unit": {
 *                                               "type": "string",
 *                                               "enum": ["celsius", "fahrenheit"]
 *                                           }
 *                                       },
 *                                       "required": ["location"]
 *                                   }
 *                               }
 *             }
 *
 *                       ]
 *
 *
 */
@Data
public class OpenAiChatRequestToolModel {
    private String type = "function";
    private Function function;


    public static OpenAiChatRequestToolModel of(String name, String description, List<String> parameters) {
        OpenAiChatRequestToolModel tool = new OpenAiChatRequestToolModel();
        Function function = new Function();
        function.setName(name);
        function.setDescription(description);
        function.setParameters(parameters);
        tool.setFunction(function);
        return tool;
    }
}
@Data
class Function {
    /**
     * 函数名称
     */
    private String name;
    /**
     * 函数描述
     */
    private String description;
    /**
     * 参数列表
     */
    private List<String> parameters;
}

class Parameter {

}