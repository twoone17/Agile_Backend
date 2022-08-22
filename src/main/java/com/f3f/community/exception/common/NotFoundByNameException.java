package com.f3f.community.exception.common;

public class NotFoundByNameException extends IllegalArgumentException {
    public NotFoundByNameException() {
        super("해당 이름으로 존재하는 객체가 없습니다");
    }

    public NotFoundByNameException(String s) {
        super(s);
    }
}
