package com.f3f.community.exception.scrapException;

public class NotFoundScrapPostListException extends IllegalArgumentException {
    public NotFoundScrapPostListException() {
        super("스크랩 포스트 리스트가 존재하지 않습니다.");
    }

    public NotFoundScrapPostListException(String s) {
        super(s);
    }
}
