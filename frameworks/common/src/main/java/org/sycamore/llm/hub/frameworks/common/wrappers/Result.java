package org.sycamore.llm.hub.frameworks.common.wrappers;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 *  @author: Sycamore
 * 全局返回对象
 */
@Data
@Accessors(chain = true)
@SuperBuilder

public class Result<T> implements Serializable {


    private static final long serialVersionUID = 5679018624309023727L;

    /**
     * 正确返回码
     */
    private Boolean success ;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求ID
     */
    private String requestId;

}