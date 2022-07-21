package com.f3f.community.exception.scrapException;

public class RebundantPostException extends Exception{
    public RebundantPostException() {
        super("컬렉션에 이미 해당 포스트가 존재합니다.");
    }

    public RebundantPostException(String message) {
        super(message);
    }
}
