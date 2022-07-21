package com.f3f.community.exception.scrapException;

public class DuplicateScrapException extends Exception {
    public DuplicateScrapException() {
        super("이미 존재하는 스크랩 컬렉션 입니다");

    }

    public DuplicateScrapException(String message) {
        super(message);
    }
}
