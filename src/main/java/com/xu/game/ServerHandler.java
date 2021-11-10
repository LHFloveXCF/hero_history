package com.xu.game;

import com.xu.game.model.BroadcastManager;
import com.xu.game.model.User;
import com.xu.game.model.UserManager;
import com.xu.game.protocol.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hero_history
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("收到客户端消息，msg = {}", msg);
        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userId = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();

            UserManager.addUser(new User(userId, heroAvatar));

            GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
            builder.setUserId(userId);
            builder.setHeroAvatar(heroAvatar);

            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

            GameMsgProtocol.UserEntryResult result = builder.build();
            BroadcastManager.broadcast(result);
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            GameMsgProtocol.WhoElseIsHereResult.Builder builder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
            for (User user : UserManager.listUser()) {
                if (null == user) {
                    continue;
                }
                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfo = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfo.setUserId(user.getUserId());
                userInfo.setHeroAvatar(user.getHeroAvatar());
                builder.addUserInfo(userInfo);
            }
            GameMsgProtocol.WhoElseIsHereResult result = builder.build();
            ctx.writeAndFlush(result);
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            if (null == userId) {
                return;
            }
            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;
            GameMsgProtocol.UserMoveToResult.Builder builder = GameMsgProtocol.UserMoveToResult.newBuilder();
            builder.setMoveUserId(userId);
            builder.setMoveToPosX(cmd.getMoveToPosX());
            builder.setMoveToPosY(cmd.getMoveToPosY());

            GameMsgProtocol.UserMoveToResult result = builder.build();
            BroadcastManager.broadcast(result);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // ctx.channel().attr(AttributeKey.valueOf("userId")).set();
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
}
