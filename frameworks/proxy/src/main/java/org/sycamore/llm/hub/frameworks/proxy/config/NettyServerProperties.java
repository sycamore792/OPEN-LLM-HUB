package org.sycamore.llm.hub.frameworks.proxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: Sycamore
 * @date: 2024/4/15 15:29
 * @version: 1.0
 * @description: netty server 配置
 */
@ConfigurationProperties(prefix = "netty-server")
@Data
public class NettyServerProperties {
    private Integer port = 8099;
    private Integer bossGroupCapacity = 1;
    private Integer workerGroupCapacity = 1;

    @Override
    public String toString() {
        return "ProxyServerProperties:\n" +
                "port=" + port ;
    }
}
