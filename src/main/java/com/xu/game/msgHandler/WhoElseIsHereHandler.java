package com.xu.game.msgHandler;

import com.xu.game.model.User;
import com.xu.game.model.UserManager;
import com.xu.game.protocol.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

/**
 * hero_history
 */
public class WhoElseIsHereHandler implements IMsgHandler<GameMsgProtocol.WhoElseIsHereCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd whoElseIsHereCmd) {
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
    }
}
