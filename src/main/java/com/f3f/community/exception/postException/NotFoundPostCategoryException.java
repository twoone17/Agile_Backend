package com.f3f.community.exception.postException;

public class NotFoundPostCategoryException extends IllegalArgumentException {
    public NotFoundPostCategoryException() {
        super("카테고리가 입력되지 않았습니다");
    }

    public NotFoundPostCategoryException(String s) {
        super(s);
    }
}
