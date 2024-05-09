package org.sycamore.llm.hub.service.netty.client.handler;

import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.sycamore.llm.hub.frameworks.model.openai.OpenAiConstants;
import org.sycamore.llm.hub.service.dto.domain.ConvertedResponseDTO;
import org.sycamore.llm.hub.service.netty.adapter.IResponseConvertAdapter;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.sycamore.llm.hub.service.toolkits.ResponseConvertUtil.convertEventSource2String;
import static org.sycamore.llm.hub.service.toolkits.ResponseConvertUtil.convertString2EventSourceChunk;

/**
 * @author: Sycamore
 * @date: 2024/4/17 13:34
 * @version: 1.0
 * @description: TODO
 */
@Slf4j
public class SimpleHttpClintInboundHandler extends SimpleChannelInboundHandler<HttpContent> {

    private final ChannelHandlerContext remoteCtx;
    private final IResponseConvertAdapter responseConvertAdapter;

    public SimpleHttpClintInboundHandler(ChannelHandlerContext remoteCtx, IResponseConvertAdapter responseConvertAdapter) {
        this.remoteCtx = remoteCtx;
        this.responseConvertAdapter = responseConvertAdapter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpContent content) throws Exception {

        ByteBuf byteBuf = content.content();
        String responseBody = byteBuf.toString(StandardCharsets.UTF_8);
        remoteCtx.writeAndFlush(new DefaultHttpContent(Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8))).addListener(ChannelFutureListener.CLOSE);
        log.info("非流式响应推送: {}", responseBody);
        ctx.close();
        remoteCtx.close();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        remoteCtx.close();
        log.info("channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Unexpected exception from downstream  {}", cause.getMessage());
        ctx.close();
    }


}
