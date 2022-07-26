package com.f3f.community.exception.postException;

public class NotFoundPostAuthorException extends IllegalArgumentException{

    public NotFoundPostAuthorException() { super("Post의 필수값인 author 값이 주어지지 않았습니다");}


    public NotFoundPostAuthorException(String message) {
            super(message);
    }

}
