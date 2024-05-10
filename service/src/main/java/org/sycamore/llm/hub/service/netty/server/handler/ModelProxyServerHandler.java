package org.sycamore.llm.hub.service.netty.server.handler;


import com.alibaba.fastjson2.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.sycamore.llm.hub.frameworks.proxy.core.ServerHandler;
import org.sycamore.llm.hub.service.dao.mapper.ModelMapper;
import org.sycamore.llm.hub.service.dto.domain.ChatReqWrapper;
import org.sycamore.llm.hub.service.dto.resp.SelectModelServerInfoByKeyRespDTO;
import org.sycamore.llm.hub.service.netty.adapter.IRequestConvertAdapter;
import org.sycamore.llm.hub.service.netty.adapter.IResponseConvertAdapter;
import org.sycamore.llm.hub.service.netty.adapter.req.OpenAiRequestConvertAdapter;
import org.sycamore.llm.hub.service.netty.adapter.resp.OpenAiResponseConvertAdapter;
import org.sycamore.llm.hub.service.netty.server.listener.ServerConnectSuccessListener;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author 桑运昌
 */
@Slf4j
@Sharable
@ServerHandler(order = 1)
@RequiredArgsConstructor
public class ModelProxyServerHandler extends SimpleChannelInboundHandler<ChatReqWrapper> {
    private final ModelMapper modelMapper;
    private final ThreadPoolTaskExecutor sqlExecutorThreadPool;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ChatReqWrapper chatReqWrapper) {
        CompletableFuture
                .supplyAsync(() ->
                        // 根据 api-key 获取对应的模型以及请求适配器、响应适配器
                        modelMapper.selectModelServerInfoByKey(chatReqWrapper.getKey(), chatReqWrapper.getChatReqDTO().getModel()), sqlExecutorThreadPool)
                .thenAcceptAsync(modelDtoList -> {
                    // 创建 success HTTP 响应
                    HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");          // 设置内容类型为text/event-stream
                    response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");                  // 设置不缓存
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);    // 保持连接


                    if (Objects.isNull(modelDtoList) || modelDtoList.isEmpty()) {
                        // 模型不存在
                        log.error("api-key:{} with model:[{}] not found in db", chatReqWrapper.getKey(), chatReqWrapper.getChatReqDTO().getModel());
                        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer("Invalid Model Name For This Request With This Key", CharsetUtil.UTF_8)))
                                .addListener(ChannelFutureListener.CLOSE);
                        return;
                    }
                    SelectModelServerInfoByKeyRespDTO selectModelServerInfoByKeyRespDTO = modelDtoList.get(0);
                    //todo 策略获取适配器对
                    IResponseConvertAdapter responseConvertAdapter = new OpenAiResponseConvertAdapter();
                    IRequestConvertAdapter requestConvertAdapter = new OpenAiRequestConvertAdapter();


                    String modelServiceUrl = selectModelServerInfoByKeyRespDTO.getModelServerBaseUrl() + "/chat/completions";
                    if (StringUtils.isNotBlank(selectModelServerInfoByKeyRespDTO.getServerParams())) {
                        //组装请求头
                        JSONArray params = JSONObject.parseObject(selectModelServerInfoByKeyRespDTO.getServerParams()).getJSONArray("params");
                        if (Objects.nonNull(params)) {
                            modelServiceUrl += "?";
                            for (int i = 0; i < params.size(); i++) {
                                JSONObject header = params.getJSONObject(i);
                                modelServiceUrl += header.getString("key") + "=" + header.getString("value");
                            }
                        }
                    }


                    // 封装请求对象
                    JSONObject jsonObject = JSONObject.from(chatReqWrapper.getChatReqDTO());
                    if (StringUtils.isNotBlank(selectModelServerInfoByKeyRespDTO.getModelServerName())) {
                        jsonObject.put("model", selectModelServerInfoByKeyRespDTO.getModelServerName());

                    }
                    if (!chatReqWrapper.getChatReqDTO().getStream()) {
                        jsonObject.put("stream", false);
                    }
                    jsonObject.remove("valid");
                    String bodyString = jsonObject.toJSONString();
                    ByteBuf requestBody = Unpooled.copiedBuffer(bodyString, CharsetUtil.UTF_8);
                    URI uri = URI.create(modelServiceUrl);
                    DefaultFullHttpRequest modelRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, modelServiceUrl, requestBody);
                    modelRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
                    modelRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    modelRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
                    modelRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, modelRequest.content().readableBytes());
                    if (StringUtils.isNotBlank(selectModelServerInfoByKeyRespDTO.getServerParams())) {
                        JSONObject params = JSONObject.parseObject(selectModelServerInfoByKeyRespDTO.getServerParams());
                        //组装请求头
                        JSONArray headers = params.getJSONArray("headers");
                        if (Objects.nonNull(headers)) {
                            for (int i = 0; i < headers.size(); i++) {
                                JSONObject header = headers.getJSONObject(i);
                                modelRequest.headers().set(header.getString("key"), header.getString("value"));
                            }
                        }
                    }

                    //写响应并绑定监听器
                    if (chatReqWrapper.getChatReqDTO().getStream()) {
                        ctx.writeAndFlush(response)
                                .addListener(
                                        new ServerConnectSuccessListener(ctx, responseConvertAdapter, modelRequest, modelServiceUrl, true)
                                );
                    } else {
                        ctx.writeAndFlush(response)
                                .addListener(
                                        new ServerConnectSuccessListener(ctx, responseConvertAdapter, modelRequest, modelServiceUrl, false)
                                );
                    }
                }, ctx.executor());


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