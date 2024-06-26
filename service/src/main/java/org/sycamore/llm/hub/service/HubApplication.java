package org.sycamore.llm.hub.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: Sycamore
 * @date: 2024/4/15 15:21
 * @version: 1.0
 * @description: 应用启动类
 */
@SpringBootApplication
@MapperScan("org.sycamore.llm.hub.service.dao.mapper")
public class HubApplication {
    public static void main(String[] args) {
        SpringApplication.run(HubApplication.class, args);
    }
}
