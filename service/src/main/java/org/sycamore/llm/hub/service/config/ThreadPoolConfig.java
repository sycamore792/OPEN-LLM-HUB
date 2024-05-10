package org.sycamore.llm.hub.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: Sycamore
 * @date: 2024/5/10 16:13
 * @version: 1.0
 * @description: TODO
 */
@Configuration
public class ThreadPoolConfig {

    @Bean("sqlExecutorThreadPool")
    public ThreadPoolTaskExecutor sqlExecutorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        // 设置最大线程数
        executor.setMaxPoolSize(40);
        // 设置队列大小
        executor.setQueueCapacity(100);
        // 设置线程活跃时间(秒)
        executor.setKeepAliveSeconds(60);
        // 设置线程名前缀+分组名称
        executor.setThreadNamePrefix("event-loop-");
        executor.setThreadGroupName("sqlExecutorThreadPool-");
        // 初始化
        executor.initialize();
        return executor;
    }
}

