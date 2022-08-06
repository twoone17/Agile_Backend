package com.f3f.community.exception.categoryException;

public class DuplicateCategoryNameException extends IllegalArgumentException {
    public DuplicateCategoryNameException() {
        super("기존에 있는 카테고리 이름과 중복입니다");
    }

    public DuplicateCategoryNameException(String s) {
        super(s);
    }
}
