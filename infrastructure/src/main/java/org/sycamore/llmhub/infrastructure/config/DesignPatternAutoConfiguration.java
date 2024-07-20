package org.sycamore.llmhub.infrastructure.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sycamore.llmhub.infrastructure.chain.AbstractChainContext;
import org.sycamore.llmhub.infrastructure.strategy.AbstractStrategyChoose;

/**
 * @author 桑运昌
 */
@Configuration
public class DesignPatternAutoConfiguration {

    /**
     * 策略模式选择器
     */
    @Bean
    public AbstractStrategyChoose abstractStrategyChoose() {
        return new AbstractStrategyChoose();
    }

    /**
     * 责任链上下文
     */
    @Bean
    public AbstractChainContext abstractChainContext() {
        return new AbstractChainContext();
    }
}
