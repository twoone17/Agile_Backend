package com.f3f.community.exception.scrapException;

public class RebundantScrapNameException extends Exception {
    public RebundantScrapNameException() {
        super("이미 존재하는 이름입니다.");
    }

    public RebundantScrapNameException(String message) {
        super(message);
    }
}
