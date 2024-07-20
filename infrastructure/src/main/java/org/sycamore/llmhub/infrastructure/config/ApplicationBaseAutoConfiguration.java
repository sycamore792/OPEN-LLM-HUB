package org.sycamore.llmhub.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sycamore.llmhub.infrastructure.common.ApplicationContentPostProcessor;
import org.sycamore.llmhub.infrastructure.common.ApplicationContextHolder;

/**
 * 应用基础自动装配
 *
 *
 * @author 桑运昌
 */
@Configuration
public class ApplicationBaseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContentPostProcessor applicationContentPostProcessor(ApplicationContext applicationContext) {
        return new ApplicationContentPostProcessor(applicationContext);
    }

//    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnProperty(value = "framework.fastjson.safa-mode", havingValue = "true")
//    public FastJsonSafeMode congoFastJsonSafeMode() {
//        return new FastJsonSafeMode();
//    }
}
