package com.f3f.community.exception.tagException;

public class DuplicateTagNameException extends IllegalArgumentException {
    public DuplicateTagNameException() {
        super("태그 이름이 중복입니다");
    }

    public DuplicateTagNameException(String s) {
        super(s);
    }
}
