package com.f3f.community.user.domain;

public enum UserLevel {
    UNBAN(1, "unban"),
    BAN(2, "ban"),
    ADMIN(3, "admin");

    private int key;
    private String value;
    UserLevel(int key, String value) {
        this.key = key;
        this.value = value;
    }


}
