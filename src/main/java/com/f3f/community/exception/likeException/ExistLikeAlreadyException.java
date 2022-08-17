package com.f3f.community.exception.likeException;

public class ExistLikeAlreadyException extends IllegalArgumentException {
    public ExistLikeAlreadyException(){super("이미 좋아요를 눌렀습니다.");}
}
