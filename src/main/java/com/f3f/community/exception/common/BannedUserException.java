package com.f3f.community.exception.common;

public class BannedUserException extends IllegalArgumentException {
    public BannedUserException() { super("차단당한 유저입니다."); }
}
