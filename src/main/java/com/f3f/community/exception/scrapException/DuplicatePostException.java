package com.f3f.community.exception.scrapException;

public class DuplicatePostException extends Exception{
    public DuplicatePostException() {
        super("컬렉션에 이미 해당 포스트가 존재합니다.");
    }

    public DuplicatePostException(String message) {
        super(message);
    }
}
