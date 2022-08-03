package com.f3f.community.exception.categoryException;

public class NotFoundChildCategoryListException extends IllegalArgumentException {
    public NotFoundChildCategoryListException() {
        super("자녀 카테고리 리스트가 null 입니다");

    }

    public NotFoundChildCategoryListException(String s) {
        super(s);
    }
}
