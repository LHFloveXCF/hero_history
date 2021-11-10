package com.xu.game.model;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * hero_history
 * 广播类
 */
public final class BroadcastManager {
    private static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private BroadcastManager() {}

    public static void addChannel(Channel channel) {
        clients.add(channel);
    }

    public static void removeChannel(Channel channel) {
        clients.remove(channel);
    }

    public static void broadcast(Object msg) {
        clients.writeAndFlush(msg);
    }
}
