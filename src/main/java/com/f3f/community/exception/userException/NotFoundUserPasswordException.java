package com.f3f.community.exception.userException;

public class NotFoundUserPasswordException extends IllegalArgumentException  {
    public NotFoundUserPasswordException() {
        super("잘못된 패스워드입니다.");
    }

    public NotFoundUserPasswordException(String message) {
        super(message);
    }
}
