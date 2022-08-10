package com.f3f.community.exception.scrapException;

public class NotFoundScrapPostByIdException extends IllegalArgumentException {
    public NotFoundScrapPostByIdException() {
        super("해당 아이디로 존재하는 스크랩 포스트가 없습니다.");
    }

    public NotFoundScrapPostByIdException(String s) {
        super(s);
    }
}
