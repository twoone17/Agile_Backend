package com.f3f.community.exception.scrapException;

public class NotFoundScrapByUserException extends IllegalArgumentException {
    public NotFoundScrapByUserException() {
        super("본인이 아닌 다른 유저의 스크랩 컬렉션 삭제 요청은 처리할 수 없습니다.");
    }

    public NotFoundScrapByUserException(String s) {
        super(s);
    }
}
