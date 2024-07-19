package org.sycamore.llmhub.core.client;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author: Sycamore
 * @date: 2024/7/19 11:41
 * @version: 1.0
 * @description: TODO
 */
@Component
@Slf4j
public class ReactorNettyClient implements BaseClientI {

    private final HttpClient clientInstance;

    @Autowired
    public ReactorNettyClient() {
        this.clientInstance = HttpClient.create(createConnectionProvider())
                .secure(spec -> {
                    try {
                        spec.sslContext(SslContextBuilder.forClient()
                                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                .build());
                    } catch (SSLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .runOn(createLoopResources())
                .responseTimeout(Duration.ofSeconds(30))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(30))
                        .addHandlerLast(new WriteTimeoutHandler(30)))
                .wiretap(true);
    }

    private static LoopResources createLoopResources() {
        int workerThreads = 4;
        return LoopResources.create("reactor-netty-client-loops", 2, workerThreads, true);
    }

    private static ConnectionProvider createConnectionProvider() {
        return ConnectionProvider.builder("custom")
                .maxConnections(50)  // 设置最大连接数
                .pendingAcquireMaxCount(100)  // 设置最大等待队列长度
                .pendingAcquireTimeout(Duration.ofSeconds(30))  // 设置最大等待时间
                .maxIdleTime(Duration.ofSeconds(20))  // 设置最大空闲时间
                .maxLifeTime(Duration.ofMinutes(2))  // 设置连接的最大生存时间
                .build();
    }


    public Disposable sendRequest(String url, String body, Map<String, String> headerMap, Consumer<String> eventHandler, Runnable onComplete) {
        return clientInstance.headers(headers -> {
                    headerMap.forEach(headers::add);
                })
                .post()
                .uri(url)
                .send((req, outbound) -> outbound.sendString(Flux.just(body)))
                .responseContent()
                .asString()
                .flatMap(content -> Flux.fromArray(content.split("data:")))
                .doOnComplete(() -> {
                    log.debug("SSE stream completed");
                    onComplete.run();
                })
                .subscribe(
                        event -> {
                            if (!StringUtils.isBlank(event)){
                                log.debug("Received event: {}", event);
                                eventHandler.accept(event);
                            }
                        },
                        error -> {
                            log.error("Error: ", error);
                        }
                );
    }
}
