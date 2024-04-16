package org.sycamore.llm.hub.frameworks.proxy.toolkits;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: Sycamore
 * @date: 2024/4/17 2:06
 * @version: 1.0
 * @description: TODO
 */
public class UriUtil {
    public static int getPortWithDefault(URI uri) {
        int port = uri.getPort();
        if (port != -1) {
            return port; // 使用 URI 中指定的端口
        }

        // 根据协议确定默认端口
        String scheme = uri.getScheme().toLowerCase();
        switch (scheme) {
            case "http":
                return 80; // HTTP 默认端口
            case "https":
                return 443; // HTTPS 默认端口
            case "ws":
                return 80; // WebSocket 默认端口 (非加密)
            case "wss":
                return 443; // WebSocket Secure 默认端口
            default:
                throw new IllegalArgumentException("Unsupported protocol: " + scheme);
        }
    }


    // 定义需要 SSL 的协议
    private static final Set<String> SSL_PROTOCOLS = new HashSet<>(
            Arrays.asList("https", "ftps", "wss"));

    /**
     * 判断给定的 URI 是否需要 SSL 处理。
     *
     * @param  uri
     * @return 如果 URI 需要 SSL 处理，则返回 true；否则返回 false。
     * @throws URISyntaxException 如果 URI 语法不正确。
     */
    public static boolean requiresSSL(URI uri) throws URISyntaxException {
        String scheme = uri.getScheme();
        return SSL_PROTOCOLS.contains(scheme);
    }


}
