package org.sycamore.llm.hub.service.netty.handler;


import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.sycamore.llm.hub.frameworks.proxy.core.ServerHandler;
import org.sycamore.llm.hub.service.dto.domain.ChatReqWrapper;
import org.sycamore.llm.hub.service.dto.req.ChatReqDTO;

import java.net.URI;

/**
 * @author 桑运昌
 */
@Slf4j
@Sharable
@ServerHandler(order = 1)
public class ModelProxyServerHandler extends SimpleChannelInboundHandler<ChatReqWrapper> {


    @Override
    public void channelRead0(ChannelHandlerContext ctx,  ChatReqWrapper chatReqWrapper) throws Exception {
        //封装客户端请求对象
        log.info("Received request: {}", chatReqWrapper);
        //todo 代理转发请求
        //构造  DefaultFullHttpRequest

        /**
         * sk-9lchi69AvehmGpx7lJ3BBTBXeMaFiQVWsYPXNQrwdtr4RwHj
         * https://api.moonshot.cn/v1/chat/completions
         */
        ByteBuf requestBody = Unpooled.copiedBuffer(JSON.toJSONString(chatReqWrapper.getChatReqDTO()), CharsetUtil.UTF_8);
        URI uri = URI.create("https://api.moonshot.cn/v1/chat/completions");
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getPath(), requestBody);

        // 设置请求头部信息
        request.headers().set(HttpHeaderNames.HOST, uri.getHost());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        request.headers().set("Authorization", "Bearer " + "sk-9lchi69AvehmGpx7lJ3BBTBXeMaFiQVWsYPXNQrwdtr4RwHj");



        // 创建HTTP响应
        ByteBuf content = Unpooled.copiedBuffer(JSON.toJSONString(chatReqWrapper), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

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