package org.sycamore.llm.hub.service.netty.adapter;

import org.sycamore.llm.hub.service.dto.domain.ConvertedResponseDTO;

/**
 * @author 桑运昌
 * @description: 抽象的响应转换适配器
 */
public interface IResponseConvertAdapter {
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
    ConvertedResponseDTO convert(String response);

}
