package com.xu.game.msgHandler;

import com.xu.game.model.BroadcastManager;
import com.xu.game.protocol.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * hero_history
 */
public class UserMoveToHandler implements IMsgHandler<GameMsgProtocol.UserMoveToCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }

        GameMsgProtocol.UserMoveToResult.Builder builder = GameMsgProtocol.UserMoveToResult.newBuilder();
        builder.setMoveUserId(userId);
        builder.setMoveToPosX(cmd.getMoveToPosX());
        builder.setMoveToPosY(cmd.getMoveToPosY());

        GameMsgProtocol.UserMoveToResult result = builder.build();
        BroadcastManager.broadcast(result);
    }
}
