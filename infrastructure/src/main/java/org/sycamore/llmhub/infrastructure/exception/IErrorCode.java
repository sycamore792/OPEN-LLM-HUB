package org.sycamore.llmhub.infrastructure.exception;

/**
 * 平台错误码
 * @author 桑运昌
 */
public interface IErrorCode {

    /**
     * 错误码
     */
    String code();

    /**
     * 错误信息
     */
    String message();
}