package com.f3f.community.exception.postException;

public class NotFoundPostInAuthorException extends IllegalArgumentException {

    public NotFoundPostInAuthorException() { super("Author의 postlist에 해당 post가 없습니다");}


    public NotFoundPostInAuthorException(String message) {
        super(message);
    }
}
