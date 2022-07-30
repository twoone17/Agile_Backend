package com.f3f.community.exception.postException;

public class NotFoundPostListByAuthor extends IllegalArgumentException {

    public NotFoundPostListByAuthor() { super("PostList를 찾을 수 있는 Author가 존재하지 않습니다");}


    public NotFoundPostListByAuthor(String message) {
        super(message);
    }
}
