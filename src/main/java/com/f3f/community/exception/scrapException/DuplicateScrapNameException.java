package com.f3f.community.exception.scrapException;

public class DuplicateScrapNameException extends IllegalArgumentException {
    public DuplicateScrapNameException() {
        super("이미 존재하는 이름입니다.");
    }

    public DuplicateScrapNameException(String message) {
        super(message);
    }
}
