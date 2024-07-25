package org.sycamore.llmhub.start;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: Sycamore
 * @date: 2024/7/19 10:57
 * @version: 1.0
 * @description: TODO
 */

@SpringBootApplication(scanBasePackages = "org.sycamore.llmhub")
@MapperScan("org.sycamore.llmhub.infrastructure.dataobject.mapper")
@EnableDubbo(scanBasePackages = "org.sycamore.llmhub.api")
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
