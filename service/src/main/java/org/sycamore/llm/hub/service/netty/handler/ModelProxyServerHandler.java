package org.sycamore.llm.hub.service.netty.handler;


import com.alibaba.fastjson2.JSON;
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
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.sycamore.llm.hub.frameworks.proxy.core.ServerHandler;
import org.sycamore.llm.hub.service.dto.domain.ChatReqWrapper;

import java.net.URI;
import java.nio.charset.StandardCharsets;

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
        //封装客户端请求对象
        log.info("Received request: {}", chatReqWrapper);
        //todo 代理转发请求
        //构造  DefaultFullHttpRequest


        String body = "{\n" +
                "     \"model\": \"moonshot-v1-8k\",\n" +
                "     \"messages\": [\n" +
                "        {\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},\n" +
                "        {\"role\": \"user\", \"content\": \"你好，我叫李雷，1+1等于多少？\"}\n" +
                "     ],\n" +
                "     \"temperature\": 0.3\n" +
                "   }";


        ByteBuf requestBody = Unpooled.copiedBuffer(JSONObject.parseObject(body).toJSONString(), CharsetUtil.UTF_8);
        URI uri = URI.create("https://api.moonshot.cn/v1/chat/completions");
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getPath(), requestBody);

        // 设置请求头部信息
        request.headers().set(HttpHeaderNames.HOST, uri.getHost());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());



        // 创建HTTP响应
        ByteBuf content = Unpooled.copiedBuffer(JSON.toJSONString(chatReqWrapper), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

        // 创建SSL上下文
        SslContext sslCtx = requiresSSL(uri) ?
                SslContextBuilder
                        .forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build()
                : null;




        ChannelFuture channelFuture = new Bootstrap().group(ctx.channel().eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        if (sslCtx != null) {
                            socketChannel.pipeline().addLast(sslCtx.newHandler(socketChannel.alloc())); // 添加 SSL 处理
                        }
                        socketChannel.pipeline().addLast(
                                new HttpClientCodec(),
                                new HttpObjectAggregator(8192),
                                new ChannelInboundHandlerAdapter() {

                                    @Override
                                    public void channelActive(ChannelHandlerContext clintCtx) throws Exception {
                                        log.info("Connected to server");
//                                        clintCtx.writeAndFlush(request);
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext clintCtx, Object msg) throws Exception {
                                        if (msg instanceof HttpResponse) {
                                            HttpResponse httpResponse = (HttpResponse) msg;
                                            log.info("Received response: {}", httpResponse);
                                        }
                                        if (msg instanceof HttpContent) {
                                            HttpContent httpContent = (HttpContent) msg;
                                            ByteBuf content = httpContent.content();
                                            log.info("Received content: {}", content.toString(StandardCharsets.UTF_8));
                                        }
                                    }
                                });
                    }
                })
                .connect(uri.getHost(),getPortWithDefault(uri)).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) {
                        if (channelFuture.isSuccess()) {
                            log.info("与模型服务连接成功,准备发送请求体");
                            // 发送请求
                            channelFuture.channel().writeAndFlush(request);
                        } else {
                            // 处理连接失败
                            channelFuture.cause().printStackTrace();
                        }
                    }
                });
//        channelFuture.addListener(ChannelFutureListener.CLOSE)

        // 发送响应并关闭连接
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

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