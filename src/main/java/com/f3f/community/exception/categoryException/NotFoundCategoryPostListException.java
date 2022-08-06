package com.f3f.community.exception.categoryException;

public class NotFoundCategoryPostListException extends IllegalArgumentException {
    public NotFoundCategoryPostListException() {
        super("카테고리 포스트리스트가 Null입니다");
    }

    public NotFoundCategoryPostListException(String s) {
        super(s);
    }
}
