package org.sycamore.llmhub.core.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.sycamore.llmhub.core.event.ModelLogInsertEvent;
import org.sycamore.llmhub.infrastructure.cache.CacheUtil;
import org.sycamore.llmhub.infrastructure.cache.DistributedCache;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelLogMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

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
    private final DistributedCache distributedCache;

    @Override
    public void onApplicationEvent(ModelLogInsertEvent event) {
        modelLogMapper.insert(event.getMessage());

        HashMap<String, String> healthyMap = new HashMap<>();
        healthyMap.put("update", LocalDateTime.now().toString());
        if (StringUtils.isBlank(event.getMessage().getErrorJson())) {
            // 非错误请求
            healthyMap.put("healthy", "1");
            distributedCache.setOrRefreshKey(CacheUtil.buildKey("model_healthy",String.valueOf(event.getMessage().getModelId())),healthyMap, Duration.ofDays(1L));

        }else {
            healthyMap.put("healthy", "0");
        }

    }
}
