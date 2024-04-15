package org.sycamore.llm.hub.frameworks.proxy.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sycamore.llm.hub.frameworks.proxy.core.MainApp;
import org.sycamore.llm.hub.frameworks.proxy.core.MainServer;

import java.util.Map;

/**
 * @author: Sycamore
 * @date: 2024/4/15 15:32
 * @version: 1.0
 * @description: TODO
 */
@Configuration
public class ProxyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NettyServerProperties nettyServerProperties() {
        return new NettyServerProperties();
    }

    @Bean
    public MainServer mainServer(NettyServerProperties nettyServerProperties) {
        return new MainServer(nettyServerProperties);
    }

    @Bean
    public MainApp mainApp(NettyServerProperties nettyServerProperties,MainServer mainServer) {
        return new MainApp(nettyServerProperties,mainServer);
    }
}
