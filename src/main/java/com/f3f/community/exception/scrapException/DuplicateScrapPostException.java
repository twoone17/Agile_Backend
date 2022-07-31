package com.f3f.community.exception.scrapException;

public class DuplicateScrapPostException extends IllegalArgumentException {
    public DuplicateScrapPostException() {
        super("이미 저장된 스크랩입니다.");

    }

    public DuplicateScrapPostException(String s) {
        super(s);
    }
}
