package org.sycamore.llm.hub.service.netty.client.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sycamore.llm.hub.service.dto.domain.ConvertedResponseDTO;
import org.sycamore.llm.hub.service.netty.adapter.IResponseConvertAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author: Sycamore
 * @date: 2024/4/17 13:34
 * @version: 1.0
 * @description: TODO
 */
@Slf4j
@RequiredArgsConstructor
public class ClintInboundHandler extends SimpleChannelInboundHandler<HttpContent> {

    private final ChannelHandlerContext remoteCtx;
    private final IResponseConvertAdapter responseConvertAdapter;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpContent content) throws Exception {
        String msg = content.content().toString(StandardCharsets.UTF_8);
        log.info("接收到消息：{}", msg);
        //todo message 协议解析

        if (!(remoteCtx.channel().isActive() && remoteCtx.channel().isWritable() && remoteCtx.channel().isOpen() && remoteCtx.channel().isRegistered())) {
            log.info("服务端侧写入异常，连接取消");
            log.info("remoteCtx.channel().isActive(): [{}]", remoteCtx.channel().isActive());
            log.info("remoteCtx.channel().isWritable(): [{}]", remoteCtx.channel().isWritable());
            log.info("remoteCtx.channel().isOpen(): [{}]", remoteCtx.channel().isOpen());
            log.info("remoteCtx.channel().isRegistered(): [{}]", remoteCtx.channel().isRegistered());
            ctx.close();
            remoteCtx.close();
            return;
        }

        ConvertedResponseDTO convert = responseConvertAdapter.convert(msg);
        if (convert.isStopFlag()){
            //发送结束响应
            remoteCtx.writeAndFlush(new DefaultHttpContent(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8)));
            log.info("收到停止标识，关闭连接");
            ctx.close();
            remoteCtx.close();
            return;
        }
        remoteCtx.writeAndFlush(new DefaultHttpContent(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8)));


    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        remoteCtx.close();
        log.info("channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Unexpected exception from downstream", cause);
        ctx.close();
    }
}
