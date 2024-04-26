package org.sycamore.llm.hub.service.netty.adapter.resp;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.PropertyNamingStrategy;
import org.springframework.stereotype.Component;
import org.sycamore.llm.hub.frameworks.model.openai.OpenAiChatResponseModel;
import org.sycamore.llm.hub.service.dto.domain.ConvertedResponseDTO;
import org.sycamore.llm.hub.service.netty.adapter.IResponseConvertAdapter;

/**
 * @author: Sycamore
 * @date: 2024/4/19 23:56
 * @version: 1.0
 * @description: TODO
 */
@Component
public class OpenAiResponseConvertAdapter implements IResponseConvertAdapter {
    private static final String END_OF_RESPONSE = "[DONE]";

    @Override
    public boolean support(String contentType) {
        return false;
    }

    @Override
    public ConvertedResponseDTO convert(String response) {
        ConvertedResponseDTO convertedResponseDTO = new ConvertedResponseDTO();
        if (response.equals(END_OF_RESPONSE)){
            convertedResponseDTO.setStopFlag(true);
            return convertedResponseDTO;
        }

        //序列化配置，定义将实体中的驼峰命名转化为下划线
        OpenAiChatResponseModel responseModel = JSONObject.parseObject(response, OpenAiChatResponseModel.class, JSONReader.Feature.SupportSmartMatch);

        convertedResponseDTO.setResponseModel(responseModel);

        return convertedResponseDTO;
    }
}
