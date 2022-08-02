package com.f3f.community.exception.scrapException;

public class NotFoundScrapPostByScrapAndPostException extends IllegalArgumentException {
    public NotFoundScrapPostByScrapAndPostException() {
        super("해당 스크랩 포스트가 리포지토리에 존재하지 않습니다");

    }

    public NotFoundScrapPostByScrapAndPostException(String s) {
        super(s);
    }
}
