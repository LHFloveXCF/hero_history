package com.xu.game;

import com.xu.game.entity.User;
import com.xu.game.protocol.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * hero_history
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    private static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final Map<Integer, User> USER_MAP = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("收到客户端消息，msg = {}", msg);
        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userId = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();

            User user = new User(userId, heroAvatar);
            USER_MAP.put(userId, user);

            GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
            builder.setUserId(userId);
            builder.setHeroAvatar(heroAvatar);

            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

            GameMsgProtocol.UserEntryResult result = builder.build();
            clients.writeAndFlush(result);
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            GameMsgProtocol.WhoElseIsHereResult.Builder builder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
            for (User user : USER_MAP.values()) {
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
            clients.writeAndFlush(result);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // ctx.channel().attr(AttributeKey.valueOf("userId")).set();
        clients.add(ctx.channel());
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
            USER_MAP.remove(userId);
            clients.remove(ctx.channel());

            GameMsgProtocol.UserQuitResult.Builder builder = GameMsgProtocol.UserQuitResult.newBuilder();
            builder.setQuitUserId(userId);

            GameMsgProtocol.UserQuitResult result = builder.build();
            clients.writeAndFlush(result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
}
