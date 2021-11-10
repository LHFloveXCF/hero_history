package com.xu.game.msgHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.xu.game.protocol.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * hero_history
 */
public final class MsgHandlerFactory {
    private static final Map<Class<?>, IMsgHandler<? extends GeneratedMessageV3>> HANDLER_MAP = new HashMap<>();
    private MsgHandlerFactory() {}

    static {
        HANDLER_MAP.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToHandler());
        HANDLER_MAP.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryHandler());
        HANDLER_MAP.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereHandler());
    }

    public static IMsgHandler<? extends GeneratedMessageV3> getHandler(Class<?> msgClazz) {
        if (null == msgClazz) {
            return null;
        }
        return HANDLER_MAP.get(msgClazz);
    }

}
