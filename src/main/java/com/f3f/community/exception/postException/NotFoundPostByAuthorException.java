package com.f3f.community.exception.postException;

public class NotFoundPostByAuthorException extends IllegalArgumentException {

    public NotFoundPostByAuthorException()
    {
        super("Author를 찾을 수 없습니다");
    }

    public NotFoundPostByAuthorException(String message){ super(message);}
}
