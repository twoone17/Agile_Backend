package com.f3f.community.exception.postException;

public class NoPostByIdException extends Exception {
    public NoPostByIdException() {
        super("해당 아이디를 가진 포스트가 존재하지 않습니다.");
    }

    public NoPostByIdException(String message) {
        super(message);
    }
}
