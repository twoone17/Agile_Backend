package com.f3f.community.exception.categoryException;

public class NotEmptyChildCategoryException extends IllegalArgumentException {
    public NotEmptyChildCategoryException() {
        super("자식 카테고리가 비어있지 않아서 삭제 할 수 없습니다");
    }

    public NotEmptyChildCategoryException(String s) {
        super(s);
    }
}
