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

    public UserLevel getUserLevelByKey(int key) {
        UserLevel returnObj;
        switch (key) {
            case 1 :
                returnObj = valueOf("UNBAN");
                break;
            case 2:
                returnObj = valueOf("BAN");
                break;
            case 3:
                returnObj = valueOf("ADMIN");
                break;
            default:
                returnObj = valueOf("UNBAN");
                break;
        }
        return returnObj;
    }
}
