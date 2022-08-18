package com.f3f.community.exception.common;

public class NotFoundByIdException extends IllegalArgumentException {
    public NotFoundByIdException() {
        super("해당 아이디로 존재하는 객체가 없습니다");
    }

    public NotFoundByIdException(String s) {
        super(s);
    }
}
