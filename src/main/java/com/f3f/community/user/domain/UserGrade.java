package com.f3f.community.user.domain;

public enum UserGrade {
    BRONZE(1, "bronze"),
    SILVER(2, "silver"),
    GOLD(3, "gold"),
    PLATINUM(4, "platinum"),
    EXPERT(5,"expert");

    private final int key;
    private final String value;

    UserGrade(int key, String value) {
        this.key = key;
        this.value = value;
    }
}
