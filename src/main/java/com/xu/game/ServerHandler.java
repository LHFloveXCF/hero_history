package com.xu.game;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hero_history
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        LOGGER.info("收到客户端消息，msg = {}", o);
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) o;
        ByteBuf byteBuf = frame.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        StringBuilder sb = new StringBuilder();
        sb.append("收到的字节-----");
        for (byte b : bytes) {
            sb.append(b);
            sb.append(", ");
        }
        LOGGER.info(sb.toString());
    }
}
