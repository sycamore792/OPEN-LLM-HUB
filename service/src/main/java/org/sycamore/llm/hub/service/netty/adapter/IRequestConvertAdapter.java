package org.sycamore.llm.hub.service.netty.adapter;

import org.sycamore.llm.hub.frameworks.model.openai.OpenAiChatRequestModel;
import org.sycamore.llm.hub.service.dto.domain.ConvertedResponseDTO;

/**
 * @author 桑运昌
 * @description: 抽象的请求转换适配器
 */
public interface IRequestConvertAdapter {
    /**
     * 适配判断方法
     * @param contentType
     * @return
     */
    boolean support(String contentType);

    /**
     * 适配转换方法
     * @param response
     * @return
     */
    Object convert(OpenAiChatRequestModel response);

}
