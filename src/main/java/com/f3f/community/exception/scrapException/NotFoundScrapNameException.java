package com.f3f.community.exception.scrapException;

public class NotFoundScrapNameException extends IllegalArgumentException {
    public NotFoundScrapNameException() {
        super("스크랩 이름이 주어지지 않았습니다.");

    }

    public NotFoundScrapNameException(String s) {
        super(s);
    }
}
