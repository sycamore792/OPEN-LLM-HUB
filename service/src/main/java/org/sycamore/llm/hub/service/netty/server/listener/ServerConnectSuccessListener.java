package org.sycamore.llm.hub.service.netty.server.listener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sycamore.llm.hub.service.netty.adapter.IResponseConvertAdapter;
import org.sycamore.llm.hub.service.netty.client.handler.SimpleHttpClintInboundHandler;
import org.sycamore.llm.hub.service.netty.client.handler.SseClintInboundHandler;

import java.net.URI;

import static org.sycamore.llm.hub.frameworks.proxy.toolkits.UriUtil.getPortWithDefault;
import static org.sycamore.llm.hub.frameworks.proxy.toolkits.UriUtil.requiresSSL;

/**
 * @author: Sycamore
 * @date: 2024/4/17 15:43
 * @version: 1.0
 * @description: TODO
 */
@Slf4j
@RequiredArgsConstructor
public class ServerConnectSuccessListener implements GenericFutureListener<Future<? super Void>> {
    private final ChannelHandlerContext ctxFromServerSide;
    private final IResponseConvertAdapter responseConvertAdapter;
    private final DefaultFullHttpRequest modelRequest;
    private final String modelServiceUrl;
    private final boolean isStream;

    @Override
    public void operationComplete(Future<? super Void> future) throws Exception {
        if (future.isSuccess()) {
            log.info("服务端侧连接已响应,客户端侧准备建立到模型服务的连接");
            // 建立到模型服务的连接
            //todo 代理转发请求


            URI uri = URI.create(modelServiceUrl);
            // 创建SSL上下文
            SslContext sslCtx = requiresSSL(uri) ?
                    SslContextBuilder
                            .forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build()
                    : null;

            ChannelFuture channelFuture = new Bootstrap().group(ctxFromServerSide.channel().eventLoop())
                    .channel(ctxFromServerSide.channel().getClass())
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            if (sslCtx != null) {
                                socketChannel.pipeline().addLast(sslCtx.newHandler(socketChannel.alloc())); // 添加 SSL 处理
                            }
                            socketChannel.pipeline().addLast(
                                    new HttpClientCodec(),
                                    new ReadTimeoutHandler(8)
                            );
                            if (isStream) {
                                socketChannel.pipeline().addLast(new SseClintInboundHandler(ctxFromServerSide, responseConvertAdapter)); //流式处理
                            }else {
                                socketChannel.pipeline().addLast(new SimpleHttpClintInboundHandler(ctxFromServerSide, responseConvertAdapter)); //非流式
                            }
                        }
                    })
                    .connect(uri.getHost(), getPortWithDefault(uri)).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) {
                            if (channelFuture.isSuccess()) {
                                log.info("客户端侧与模型服务连接成功,准备发送请求体");
                                // 发送请求
                                channelFuture.channel().writeAndFlush(modelRequest);
                            } else {
                                // 处理连接失败
                                channelFuture.cause().printStackTrace();
                            }
                        }
                    });
        } else {
            // 处理发送失败的情况
            future.cause().printStackTrace();
            ctxFromServerSide.close();
        }
    }
}
