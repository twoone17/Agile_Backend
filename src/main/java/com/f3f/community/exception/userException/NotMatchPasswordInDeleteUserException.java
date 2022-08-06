package com.f3f.community.exception.userException;

public class NotMatchPasswordInDeleteUserException extends IllegalArgumentException{
    public NotMatchPasswordInDeleteUserException() {
        super("다른 유저의 패스워드입니다.");
    }
}
