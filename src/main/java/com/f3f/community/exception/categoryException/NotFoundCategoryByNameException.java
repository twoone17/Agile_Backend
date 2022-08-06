package com.f3f.community.exception.categoryException;

public class NotFoundCategoryByNameException extends IllegalArgumentException {
    public NotFoundCategoryByNameException() {
        super("해당 이름으로 존재하는 카테고리를 찾을 수 없습니다");
    }

    public NotFoundCategoryByNameException(String s) {
        super(s);
    }
}
