package org.sycamore.llmhub.core.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.sycamore.llmhub.infrastructure.dataobject.ModelLog;

/**
 * 模型日志插入事件
 * @author 桑运昌
 */
@Getter
public class ModelLogInsertEvent extends ApplicationEvent {
    private final ModelLog message;

    public ModelLogInsertEvent(Object source, ModelLog message) {
        super(source);
        this.message = message;
    }

}