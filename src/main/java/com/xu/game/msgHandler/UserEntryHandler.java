package com.xu.game.msgHandler;

import com.xu.game.model.BroadcastManager;
import com.xu.game.model.User;
import com.xu.game.model.UserManager;
import com.xu.game.protocol.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * hero_history
 */
public class UserEntryHandler implements IMsgHandler<GameMsgProtocol.UserEntryCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        int userId = cmd.getUserId();
        String heroAvatar = cmd.getHeroAvatar();

        UserManager.addUser(new User(userId, heroAvatar));

        GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
        builder.setUserId(userId);
        builder.setHeroAvatar(heroAvatar);

        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

        GameMsgProtocol.UserEntryResult result = builder.build();
        BroadcastManager.broadcast(result);
    }
}
