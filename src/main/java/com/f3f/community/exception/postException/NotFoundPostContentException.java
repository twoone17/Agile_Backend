package com.f3f.community.exception.postException;

public class NotFoundPostContentException extends IllegalArgumentException {

    public NotFoundPostContentException() { super("Post의 필수값인 Content 값이 주어지지 않았습니다");}


    public NotFoundPostContentException(String message) {
        super(message);
    }
}
