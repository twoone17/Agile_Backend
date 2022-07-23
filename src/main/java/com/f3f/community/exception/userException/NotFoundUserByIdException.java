package com.f3f.community.exception.userException;

public class NotFoundUserByIdException extends IllegalArgumentException {
    public NotFoundUserByIdException() {
        super("해당 아이디로 존재하는 유저가 없습니다.");
    }

    public NotFoundUserByIdException(String s) {
        super(s);
    }
}
