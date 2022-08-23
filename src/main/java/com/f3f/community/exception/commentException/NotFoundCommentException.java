package com.f3f.community.exception.commentException;

public class NotFoundCommentException extends IllegalArgumentException{
    public NotFoundCommentException(){super("존재하지 않는 댓글입니다.");}
}
