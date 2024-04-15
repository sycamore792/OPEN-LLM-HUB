package org.sycamore.llm.hub.frameworks.proxy.core;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author 桑运昌
 * @description: 用户自定义服务处理器接口
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface ServerHandler {
    int order();
}
