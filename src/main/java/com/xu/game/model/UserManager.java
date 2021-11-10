package com.xu.game.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * hero_history
 * 玩家管理类
 */
public final class UserManager {
    private static final Map<Integer, User> USER_MAP = new HashMap<>();
    private UserManager() {}

    public static void addUser(User user) {
        if (null == user) {
            return;
        }
        USER_MAP.put(user.getUserId(), user);
    }

    public static void removeUserByUserId(int userId) {
        USER_MAP.remove(userId);
    }

    public static Collection<User> listUser() {
        return USER_MAP.values();
    }


}
