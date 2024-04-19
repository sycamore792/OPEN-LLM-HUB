package org.sycamore.llm.hub.service.netty.server.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.sycamore.llm.hub.frameworks.proxy.core.ServerHandler;
import org.sycamore.llm.hub.service.dto.domain.ChatReqWrapper;
import org.sycamore.llm.hub.service.dto.req.ChatReqDTO;

/**
 * @author: Sycamore
 * @date: 2024/4/16 17:11
 * @version: 1.0
 * @description: 模型外部接口统一网关
 */
@Slf4j
@ServerHandler(order = 0)
@Sharable
public class GatewayServiceHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String URI_PREFIX = "/v1/chat/completions";
    private static final String HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!isValidUri(request)) {
            // 非法资源请求
            // 写入响应并添加一个监听器在写入完成后关闭连接
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer("Invalid URI Or Request Method", CharsetUtil.UTF_8)))
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }
        if (!isValidHeader(request)){
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.copiedBuffer("Invalid Request header", CharsetUtil.UTF_8)))
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }
        String requestBodyString = request.content().toString(CharsetUtil.UTF_8);
        if (!JSON.isValidObject(requestBodyString)){
            log.error("request forbidden, request body is not valid");
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer("Invalid Request body", CharsetUtil.UTF_8)))
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }
        log.info("request body: {}", requestBodyString);
        ChatReqDTO chatReqDTO = JSON.parseObject(requestBodyString, ChatReqDTO.class);
        if (!chatReqDTO.isValid()){
            log.error("request forbidden, request body is not valid");
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer("Invalid Request body", CharsetUtil.UTF_8)))
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }

        ChatReqWrapper chatReqWrapper = new ChatReqWrapper();
        chatReqWrapper.setChatReqDTO(chatReqDTO);
        chatReqWrapper.setKey(request.headers().get(HEADER_NAME).substring(AUTHORIZATION_PREFIX.length()));
        // 将转发参数对象传递到下一个处理器
        ctx.fireChannelRead(chatReqWrapper);

    }

    private Boolean isValidUri(FullHttpRequest request) {
        if (!request.uri().startsWith(URI_PREFIX)) {
            log.error("request forbidden，URI：[{}] is not valid", request.uri());
            return false;
        }
        // 检查HTTP方法
        if (!HttpMethod.POST.equals(request.method())) {
            log.error("request forbidden, need POST method, not [{}]", request.method());
            return false;
        }
        return true;
    }

    private Boolean isValidHeader(FullHttpRequest request) {
        HttpHeaders headers = request.headers();

        // 从请求头中获取Authorization字段
        String authorizationHeader = headers.get(HEADER_NAME);

        // 检查Authorization请求头是否存在以及它是否以"Bearer "开头
        if (authorizationHeader != null && authorizationHeader.startsWith(AUTHORIZATION_PREFIX)) {
            // 提取令牌
            String token = authorizationHeader.substring("Bearer ".length());

            //todo 验证
            log.info("request's authorization: [{}]", authorizationHeader);

            // 记得在此之后添加业务逻辑处理
            return true;
        } else {
            log.error("Request dose not have Authorization header, request info :[{}]",request);
            return false;
        }
    }

}
