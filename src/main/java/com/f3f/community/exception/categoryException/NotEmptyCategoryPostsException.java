package com.f3f.community.exception.categoryException;

public class NotEmptyCategoryPostsException extends IllegalArgumentException {
    public NotEmptyCategoryPostsException() {
        super("카테고리에 포스트가 아직 존재하여서 삭제할 수 없습니다");
    }

    public NotEmptyCategoryPostsException(String s) {
        super(s);
    }
}
