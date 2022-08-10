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

    public UserGrade getUserGradeByKey(int key) {
        UserGrade returnObj;
        switch (key) {
            case 1 :
                returnObj = valueOf("BRONZE");
                break;
            case 2 :
                returnObj = valueOf("SILVER");
                break;
            case 3 :
                returnObj = valueOf("GOLD");
                break;
            case 4 :
                returnObj = valueOf("PLATINUM");
                break;
            case 5 :
                returnObj = valueOf("EXPERT");
                break;
            default:
                returnObj = valueOf("BRONZE");
                break;
        }
        return returnObj;
    }
}
