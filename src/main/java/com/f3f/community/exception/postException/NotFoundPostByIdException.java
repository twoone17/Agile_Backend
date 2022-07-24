package com.f3f.community.exception.postException;

public class NotFoundPostByIdException extends IllegalArgumentException {
    public NotFoundPostByIdException() {
        super("해당 아이디를 가진 포스트가 존재하지 않습니다.");
    }

    public NotFoundPostByIdException(String message) {
        super(message);
    }
}
