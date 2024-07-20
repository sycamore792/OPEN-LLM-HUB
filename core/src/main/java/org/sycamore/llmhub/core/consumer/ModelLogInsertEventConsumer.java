package org.sycamore.llmhub.core.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.sycamore.llmhub.core.event.ModelLogInsertEvent;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelLogMapper;

/**
 * @author: Sycamore
 * @date: 2024/7/21 2:30
 * @version: 1.0
 * @description: TODO
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ModelLogInsertEventConsumer implements ApplicationListener<ModelLogInsertEvent> {
    private final ModelLogMapper modelLogMapper;

    @Override
    public void onApplicationEvent(ModelLogInsertEvent event) {
        modelLogMapper.insert(event.getMessage());
    }
}
