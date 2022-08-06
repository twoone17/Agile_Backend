package com.f3f.community.exception.categoryException;

public class NotFoundCategoryByIdException extends IllegalArgumentException {
    public NotFoundCategoryByIdException() {
        super("해당 아이디로 존재하는 카테고리가 없습니다.");
    }

    public NotFoundCategoryByIdException(String s) {
        super(s);
    }
}
