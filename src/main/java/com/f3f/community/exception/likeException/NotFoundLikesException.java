package com.f3f.community.exception.likeException;

public class NotFoundLikesException extends IllegalArgumentException{
    public NotFoundLikesException(){ super("존재하지 않는 좋아요입니다.");}
}
