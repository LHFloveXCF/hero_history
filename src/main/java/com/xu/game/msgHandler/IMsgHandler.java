package com.xu.game.msgHandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

public interface IMsgHandler<T extends GeneratedMessageV3> {
    void handle(ChannelHandlerContext ctx, T t);
}
