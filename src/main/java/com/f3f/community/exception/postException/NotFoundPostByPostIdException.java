package com.f3f.community.exception.postException;

public class NotFoundPostByPostIdException extends IllegalArgumentException {

    public NotFoundPostByPostIdException()
    {
        super("Post Repository에 해당하는 아이디의 Post가 없습니다");
    }

    public NotFoundPostByPostIdException(String message){ super(message);}
}
