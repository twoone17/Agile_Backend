package com.f3f.community.exception.postException;

public class NoPostByPostIdException extends IllegalArgumentException {

    public NoPostByPostIdException()
    {
        super("Post Repository에 해당하는 아이디의 Post가 없습니다");
    }

    public NoPostByPostIdException(String message){ super(message);}
}
