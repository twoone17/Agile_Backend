package com.f3f.community.exception.postException;

public class NotFoundPostTitleException extends IllegalArgumentException {

    public NotFoundPostTitleException() { super("Post의 필수값인 title 값이 주어지지 않았습니다");}


    public NotFoundPostTitleException(String message) {
        super(message);
    }
}
