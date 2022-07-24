package com.f3f.community.exception.postException;

public class NoPostByIdException extends Exception {

    public NoPostByIdException()
    {
        super("Post Repository에 해당하는 아이디의 Post가 없습니다");
    }

    public NoPostByIdException(String message){ super(message);}
}
