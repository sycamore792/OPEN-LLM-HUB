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
public class ClintInboundHandler extends SimpleChannelInboundHandler<HttpContent> {

    private final ChannelHandlerContext remoteCtx;
    private final IResponseConvertAdapter responseConvertAdapter;
    private final ByteBuf msgBuffer = Unpooled.buffer();
    private boolean lastEventEndedWithrn = false; // 用于记录上一个事件的结束符类型

    public ClintInboundHandler(ChannelHandlerContext remoteCtx, IResponseConvertAdapter responseConvertAdapter) {
        this.remoteCtx = remoteCtx;
        this.responseConvertAdapter = responseConvertAdapter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpContent content) throws Exception {
        ByteBuf byteBuf = content.content();
        msgBuffer.writeBytes(byteBuf);
        processBuffer(ctx);
    }

    private void processBuffer(ChannelHandlerContext ctx) {
        while (msgBuffer.isReadable()) {
            // 检查是否存在完整的 SSE 事件
            int endIndex = findEndOfEvent(msgBuffer);
            if (endIndex == -1) {
                break;
            }

            // 复制事件数据到新的 ByteBuf
            ByteBuf eventBuf = msgBuffer.readSlice(endIndex);
            String event = eventBuf.toString(StandardCharsets.UTF_8);

            // 处理事件
            log.info("接收到消息：{}", event);
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

            ConvertedResponseDTO convert = responseConvertAdapter.convert(convertEventSource2String(event));
            if (convert.isStopFlag()) {
                //发送结束响应
                remoteCtx.writeAndFlush(new DefaultHttpContent(Unpooled.copiedBuffer(convertString2EventSourceChunk(OpenAiConstants.DEFAULT_STOP_WORD), CharsetUtil.UTF_8)));
                log.info("收到停止标识，关闭连接");
                ctx.close();
                remoteCtx.close();
                return;
            }

            remoteCtx.writeAndFlush(new DefaultHttpContent(Unpooled.copiedBuffer(convertString2EventSourceChunk(JSONObject.toJSONString(convert.getResponseModel())), CharsetUtil.UTF_8)));


            // 跳过结束符
            msgBuffer.skipBytes(endOfEventLength());
        }
    }

    private int findEndOfEvent(ByteBuf buffer) {
        // 首先尝试查找 \r\n\r\n
        int rnIndex = ByteBufUtil.indexOf(Unpooled.wrappedBuffer(new byte[]{'\r', '\n', '\r', '\n'}), buffer.slice());
        if (rnIndex != -1) {
            lastEventEndedWithrn = true;
            return rnIndex;
        }

        // 然后查找 \n\n
        int nIndex = ByteBufUtil.indexOf(Unpooled.wrappedBuffer(new byte[]{'\n', '\n'}), buffer.slice());
        if (nIndex != -1) {
            lastEventEndedWithrn = false;
            return nIndex;
        }
        return -1;
    }

    private int endOfEventLength() {
        return lastEventEndedWithrn ? 4 : 2; // \r\n\r\n 或 \n\n
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
