package com.f3f.community.exception.scrapException;

public class RebundantScrapException extends Exception {
    public RebundantScrapException() {
        super("이미 존재하는 스크랩 컬렉션 입니다");

    }

    public RebundantScrapException(String message) {
        super(message);
    }
}
