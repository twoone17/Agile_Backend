package com.f3f.community.user.domain;

public enum UserLevel {
    UNBAN(1, "unban"),
    BAN(2, "ban");

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
            default:
                returnObj = valueOf("UNBAN");
                break;
        }
        return returnObj;
    }
}
