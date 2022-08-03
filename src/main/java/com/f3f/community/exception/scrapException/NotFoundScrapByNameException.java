package com.f3f.community.exception.scrapException;

public class NotFoundScrapByNameException extends IllegalArgumentException{
    public NotFoundScrapByNameException() {
        super("해당 이름으로 존재하는 스크랩이 없습니다.");
    }

    public NotFoundScrapByNameException(String s) {
        super(s);
    }
}
