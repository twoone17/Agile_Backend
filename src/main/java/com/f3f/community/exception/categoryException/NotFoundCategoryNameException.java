package com.f3f.community.exception.categoryException;

public class NotFoundCategoryNameException extends IllegalArgumentException {
    public NotFoundCategoryNameException() {
        super("카테고리 이름이 null 입니다");
    }

    public NotFoundCategoryNameException(String s) {
        super(s);
    }
}
