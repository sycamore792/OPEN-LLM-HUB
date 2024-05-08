package org.sycamore.llm.hub.service.netty.client.handler;

import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.sycamore.llm.hub.frameworks.model.openai.OpenAiConstants;
import org.sycamore.llm.hub.service.dto.domain.ConvertedResponseDTO;
import org.sycamore.llm.hub.service.netty.adapter.IResponseConvertAdapter;

import java.nio.charset.StandardCharsets;

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
        String string = byteBuf.toString(StandardCharsets.UTF_8);
        remoteCtx.writeAndFlush(new DefaultHttpContent(byteBuf));
        ctx.close();
//        remoteCtx.close();
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
