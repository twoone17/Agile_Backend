package com.f3f.community.exception.scrapException;

public class NotFoundScrapUserException extends IllegalArgumentException {
    public NotFoundScrapUserException() {
        super("스크랩 유저가 선언되지 않았습니다.");
    }

    public NotFoundScrapUserException(String s) {
        super(s);
    }
}
