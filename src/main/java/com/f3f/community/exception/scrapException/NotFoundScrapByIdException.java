package com.f3f.community.exception.scrapException;

public class NotFoundScrapByIdException extends IllegalArgumentException{

    public NotFoundScrapByIdException() {
        super("Scrap Repository에 해당 아이디로 존재하는 스크랩이 없습니다.");
    }

    public NotFoundScrapByIdException(String message) {
        super(message);
    }
}
