package com.xu.game;

import com.google.protobuf.GeneratedMessageV3;
import com.xu.game.model.BroadcastManager;
import com.xu.game.model.UserManager;
import com.xu.game.msgHandler.IMsgHandler;
import com.xu.game.msgHandler.MsgHandlerFactory;
import com.xu.game.protocol.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hero_history
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof GeneratedMessageV3)) {
            return;
        }
        LOGGER.info("收到客户端消息，msg = {}", msg);
        IMsgHandler<? extends GeneratedMessageV3> handler = MsgHandlerFactory.getHandler(msg.getClass());
        if (null != handler) {
            handler.handle(ctx, cast(msg));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        BroadcastManager.addChannel(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return;
        }
        try {
            super.handlerRemoved(ctx);
            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

            LOGGER.info("---------------{}", userId);

            if (null == userId) {
                return;
            }

            UserManager.removeUserByUserId(userId);

            BroadcastManager.removeChannel(ctx.channel());

            GameMsgProtocol.UserQuitResult.Builder builder = GameMsgProtocol.UserQuitResult.newBuilder();
            builder.setQuitUserId(userId);

            GameMsgProtocol.UserQuitResult result = builder.build();
            BroadcastManager.broadcast(result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    /**
     * 将消息造型
     *
     * @param msg   待造型的消息
     * @param <T>   造型类型
     * @return  null or instance
     */
    private static <T extends GeneratedMessageV3> T cast(Object msg) {
        if (null == msg) {
            return null;
        }
        return (T) msg;
    }
}
