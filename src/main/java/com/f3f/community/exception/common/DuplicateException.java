package com.f3f.community.exception.common;

public class DuplicateException extends IllegalArgumentException {
    public DuplicateException() {
        super("중복으로 인해서 생성할 수 없습니다.");
    }

    public DuplicateException(String s) {
        super(s);
    }
}
