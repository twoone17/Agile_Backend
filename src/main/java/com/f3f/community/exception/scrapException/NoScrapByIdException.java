package com.f3f.community.exception.scrapException;

public class NoScrapByIdException extends Exception{

    public NoScrapByIdException() {
        super("Scrap Repository에 해당 아이디로 존재하는 스크랩이 없습니다.");
    }

    public NoScrapByIdException(String message) {
        super(message);
    }
}
