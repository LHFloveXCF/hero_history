package com.xu.game.model;

/**
 * hero_history
 */

public class User {
    private int userId;
    private String heroAvatar;

    public User(int userId, String heroAvatar) {
        this.userId = userId;
        this.heroAvatar = heroAvatar;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", heroAvatar='" + heroAvatar + '\'' +
                '}';
    }
}
