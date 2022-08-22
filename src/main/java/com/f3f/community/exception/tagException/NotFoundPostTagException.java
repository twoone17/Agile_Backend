package com.f3f.community.exception.tagException;

public class NotFoundPostTagException extends IllegalArgumentException {
    public NotFoundPostTagException() {
        super("해당 포스트에는 해당 태그가 존재하지 않습니다.");
    }

    public NotFoundPostTagException(String s) {
        super(s);
    }
}
