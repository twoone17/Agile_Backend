package com.f3f.community.user.domain;

import lombok.Getter;

public enum UserGrade {
    BRONZE(1, "bronze"),
    SILVER(2, "silver"),
    GOLD(3, "gold"),
    PLATINUM(4, "platinum"),
    EXPERT(5,"expert");

    private int key;
    private String value;

    UserGrade(int key, String value) {
        this.key = key;
        this.value = value;
    }
//
//    public int getKey() {
//        return key;
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public UserGrade Upgrade(int key) {
//        // key를 새로 할당해줌으로서 update.
//        this.key = key;
//        return valueOf(this.);
//    }
}
