package com.f3f.community.exception.postException;

public class NotFoundPostByUserIdException extends IllegalArgumentException {

    public NotFoundPostByUserIdException() { super("UserId로 post를 찾을수 없습니다");}


    public NotFoundPostByUserIdException(String message) {
        super(message);
    }
}
