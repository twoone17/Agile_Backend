package com.f3f.community.exception.commentException;

public class NotEmptyChildCommentException extends IllegalArgumentException{
    public NotEmptyChildCommentException() { super("자식 댓글이 존재합니다.");}
}
