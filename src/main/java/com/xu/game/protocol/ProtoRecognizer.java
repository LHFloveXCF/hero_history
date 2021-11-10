package com.xu.game.protocol;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 * hero_history
 */
public final class ProtoRecognizer {
    private static final Map<Integer, GeneratedMessageV3> MESSAGE_MAP = new HashMap<>();
    private static final Map<Class<?>, Integer> CODE_MAP = new HashMap<>();

    private ProtoRecognizer() {
    }

    static {
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();
        for (Class<?> innerClazz : innerClazzArray) {
            if (null == innerClazz || !GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                continue;
            }
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();
            for (GameMsgProtocol.MsgCode code : GameMsgProtocol.MsgCode.values()) {
                if (null == code) {
                    continue;
                }
                String codeName = code.name();
                codeName = codeName.replaceAll("_", "");
                codeName = codeName.toLowerCase();

                if (!clazzName.startsWith(codeName)) {
                    continue;
                }

                try {
                    Object instance = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);
                    MESSAGE_MAP.put(code.getNumber(), (GeneratedMessageV3) instance);
                    CODE_MAP.put(innerClazz, code.getNumber());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Message.Builder getMessageByCode(int code) {
        GeneratedMessageV3 message = MESSAGE_MAP.get(code);
        return null == message ? null : message.newBuilderForType();
    }

    public static int getCodeByMessage(Class<?> clazz) {
        if (null == clazz) {
            return -1;
        }
        Integer integer = CODE_MAP.get(clazz);
        return null == integer ? -1 : integer;
    }
}
