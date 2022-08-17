package com.f3f.community.exception.commentException;

public class NotFoundCommentByIdException extends IllegalArgumentException{
    public NotFoundCommentByIdException(){super("존재하지 않는 댓글 아이디입니다.");}
}
