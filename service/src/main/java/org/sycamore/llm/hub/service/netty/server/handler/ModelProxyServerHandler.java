package org.sycamore.llm.hub.service.netty.server.handler;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sycamore.llm.hub.frameworks.proxy.core.ServerHandler;
import org.sycamore.llm.hub.service.dao.entity.ApiKeyModelDO;
import org.sycamore.llm.hub.service.dto.domain.ChatReqWrapper;
import org.sycamore.llm.hub.service.netty.adapter.IRequestConvertAdapter;
import org.sycamore.llm.hub.service.netty.adapter.IResponseConvertAdapter;
import org.sycamore.llm.hub.service.netty.adapter.req.OpenAiRequestConvertAdapter;
import org.sycamore.llm.hub.service.netty.adapter.resp.OpenAiResponseConvertAdapter;
import org.sycamore.llm.hub.service.netty.server.listener.ServerConnectSuccessListener;
import org.sycamore.llm.hub.service.service.IApiKeyModelService;

import java.net.URI;

/**
 * @author 桑运昌
 */
@Slf4j
@Sharable
@ServerHandler(order = 1)
@RequiredArgsConstructor
public class ModelProxyServerHandler extends SimpleChannelInboundHandler<ChatReqWrapper> {
    private final IApiKeyModelService apiKeyModelService;


    @Override
    public void channelRead0(ChannelHandlerContext ctx, ChatReqWrapper chatReqWrapper){
        // 创建 success HTTP 响应
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");          // 设置内容类型为text/event-stream
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");                  // 设置不缓存
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);    // 保持连接


        // 根据 api-key 获取对应的模型以及请求适配器、响应适配器
        ApiKeyModelDO apiKeyModelDO = apiKeyModelService.getOne(Wrappers.lambdaQuery(ApiKeyModelDO.class)
                .eq(ApiKeyModelDO::getApiKey, chatReqWrapper.getKey())
                .eq(ApiKeyModelDO::getModelName, chatReqWrapper.getChatReqDTO().getModel())
        );
        //todo 策略获取适配器对
        IResponseConvertAdapter responseConvertAdapter = new OpenAiResponseConvertAdapter();
        IRequestConvertAdapter requestConvertAdapter = new OpenAiRequestConvertAdapter();



        String modelServiceUrl = apiKeyModelDO.getServiceUrl();


        // 封装请求对象
        JSONObject jsonObject = JSONObject.from(chatReqWrapper.getChatReqDTO());
        jsonObject.put("stream", true);
        jsonObject.put("model", apiKeyModelDO.getModelName());
        ByteBuf requestBody = Unpooled.copiedBuffer(jsonObject.toJSONString(), CharsetUtil.UTF_8);
        URI uri = URI.create(modelServiceUrl);
        DefaultFullHttpRequest modelRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getPath(), requestBody);
        modelRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
        modelRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        modelRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        modelRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, modelRequest.content().readableBytes());
        modelRequest.headers().set("Authorization", "Bearer " + "122ae65f856b41c1aa2bfef31b0b6362");


        //写响应并绑定监听器
        ctx.writeAndFlush(response)
           .addListener(
                   new ServerConnectSuccessListener(ctx,responseConvertAdapter,modelRequest,modelServiceUrl)
           );

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
        // 记录日志或异常处理
        log.error("An exception occurred in the channel: {}", cause.getMessage());
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer("System error", CharsetUtil.UTF_8)))
                .addListener(ChannelFutureListener.CLOSE);
    }
}