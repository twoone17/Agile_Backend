package com.f3f.community.exception.commentException;

public class NotFoundParentCommentException extends IllegalArgumentException{
    public NotFoundParentCommentException(){ super("부모 댓글이 존재하지 않습니다.");}
}
