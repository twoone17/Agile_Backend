package com.f3f.community.exception.categoryException;

public class NotFoundParentCategoryException extends IllegalArgumentException {
    public NotFoundParentCategoryException() {
        super("부모 카테고리를 찾을 수 없습니다");
    }

    public NotFoundParentCategoryException(String s) {
        super(s);
    }
}
