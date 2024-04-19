package org.sycamore.llm.hub.service.netty.server.handler;


import com.alibaba.fastjson2.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.sycamore.llm.hub.frameworks.proxy.core.ServerHandler;
import org.sycamore.llm.hub.service.dto.domain.ChatReqWrapper;
import org.sycamore.llm.hub.service.netty.adapter.IResponseConvertAdapter;
import org.sycamore.llm.hub.service.netty.adapter.OpenAiResponseConvertAdapter;
import org.sycamore.llm.hub.service.netty.client.handler.ClintInboundHandler;
import org.sycamore.llm.hub.service.netty.server.listener.ServerConnectSuccessListener;

import java.net.URI;
import static org.sycamore.llm.hub.frameworks.proxy.toolkits.UriUtil.getPortWithDefault;
import static org.sycamore.llm.hub.frameworks.proxy.toolkits.UriUtil.requiresSSL;

/**
 * @author 桑运昌
 */
@Slf4j
@Sharable
@ServerHandler(order = 1)
public class ModelProxyServerHandler extends SimpleChannelInboundHandler<ChatReqWrapper> {


    @Override
    public void channelRead0(ChannelHandlerContext ctx, ChatReqWrapper chatReqWrapper) throws Exception {
        // 创建 success HTTP 响应
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");          // 设置内容类型为text/event-stream
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");                  // 设置不缓存
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);    // 保持连接


        // 根据 api-key 获取对应的模型以及响应适配器
        IResponseConvertAdapter responseConvertAdapter = new OpenAiResponseConvertAdapter();


        // 封装请求对象
        JSONObject jsonObject = JSONObject.from(chatReqWrapper.getChatReqDTO());
        jsonObject.put("stream", true);
        ByteBuf requestBody = Unpooled.copiedBuffer(jsonObject.toJSONString(), CharsetUtil.UTF_8);
        URI uri = URI.create("http://58.34.1.32:8881/v1/chat/completions");
        DefaultFullHttpRequest modelRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getPath(), requestBody);
        modelRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
        modelRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        modelRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        modelRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, modelRequest.content().readableBytes());
        modelRequest.headers().set("Authorization", "Bearer " + "sk-9lchi69AvehmGpx7lJ3BBTBXeMaFiQVWsYPXNQrwdtr4RwHj");


        //写响应并绑定监听器
        ctx.writeAndFlush(response) .addListener(new ServerConnectSuccessListener(ctx,responseConvertAdapter,modelRequest,"http://58.34.1.32:8881/v1/chat/completions"))
//                .addListener(new GenericFutureListener<Future<? super Void>>() {
//            @Override
//            public void operationComplete(Future<? super Void> future) throws Exception {
//                if (future.isSuccess()) {
//                    log.info("服务端侧连接已响应,客户端侧准备建立到模型服务的连接");
//                    // 建立到模型服务的连接
//                    //todo 代理转发请求
//
//                    JSONObject jsonObject = JSONObject.from(chatReqWrapper.getChatReqDTO());
//                    jsonObject.put("stream", true);
//
//                    ByteBuf requestBody = Unpooled.copiedBuffer(jsonObject.toJSONString(), CharsetUtil.UTF_8);
//                    URI uri = URI.create("http://58.34.1.32:8881/v1/chat/completions");
//                    DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getPath(), requestBody);
//
//                    // 设置请求头部信息
//                    request.headers().set(HttpHeaderNames.HOST, uri.getHost());
//                    request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//                    request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
//                    request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
//                    request.headers().set("Authorization", "Bearer " + "sk-9lchi69AvehmGpx7lJ3BBTBXeMaFiQVWsYPXNQrwdtr4RwHj");
//
//
//                    // 创建SSL上下文
//                    SslContext sslCtx = requiresSSL(uri) ?
//                            SslContextBuilder
//                                    .forClient()
//                                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
//                                    .build()
//                            : null;
//
//
//                    ChannelFuture channelFuture = new Bootstrap().group(ctx.channel().eventLoop())
//                            .channel(ctx.channel().getClass())
//                            .handler(new ChannelInitializer<SocketChannel>() {
//                                @Override
//                                protected void initChannel(SocketChannel socketChannel) throws Exception {
//                                    if (sslCtx != null) {
//                                        socketChannel.pipeline().addLast(sslCtx.newHandler(socketChannel.alloc())); // 添加 SSL 处理
//                                    }
//                                    socketChannel.pipeline().addLast(
//                                            new HttpClientCodec(),
//                                            new ReadTimeoutHandler(8),
//                                            new ClintInboundHandler(ctx,responseConvertAdapter));
//                                }
//                            })
//                            .connect(uri.getHost(), getPortWithDefault(uri)).addListener(new ChannelFutureListener() {
//                                @Override
//                                public void operationComplete(ChannelFuture channelFuture) {
//                                    if (channelFuture.isSuccess()) {
//                                        log.info("客户端侧与模型服务连接成功,准备发送请求体");
//                                        // 发送请求
//                                        channelFuture.channel().writeAndFlush(request);
//                                    } else {
//                                        // 处理连接失败
//                                        channelFuture.cause().printStackTrace();
//                                    }
//                                }
//                            });
//                } else {
//                    // 处理发送失败的情况
//                    future.cause().printStackTrace();
//                    ctx.close();
//                }
//            }
//        })


        ;

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 处理在处理入站事件时发生的异常。
     *
     * @param ctx   ChannelHandlerContext，提供了操作网络通道的方法。
     * @param cause 异常对象。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 调用父类的 exceptionCaught 方法，它将按照 ChannelPipeline 中的下一个处理器继续处理异常
        super.exceptionCaught(ctx, cause);
        // 打印自定义消息，实际使用时应该记录日志或进行更复杂的异常处理
        System.out.println("----");
    }
}