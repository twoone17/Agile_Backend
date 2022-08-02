package com.f3f.community.exception.scrapException;

public class NotFoundNewScrapNameException extends IllegalArgumentException {
    public NotFoundNewScrapNameException() {
        super("변경할 스크랩 네임이 null입니다");
    }

    public NotFoundNewScrapNameException(String s) {
        super(s);
    }
}
