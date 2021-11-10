package com.xu.game.protocol;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * hero_history
 */
public class ProtoDecoder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        ByteBuf byteBuf = frame.content();
        byteBuf.readShort();
        int msgCode = byteBuf.readShort();

        Message.Builder message = ProtoRecognizer.getMessageByCode(msgCode);
        if (null == message) {
            return;
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        message.clear();
        message.mergeFrom(bytes);
        Message cmd = message.build();
        if (null != cmd) {
            ctx.fireChannelRead(cmd);
        }
    }
}
