package com.f3f.community.exception.postException;
public class NotFoundPostListByTitle extends IllegalArgumentException {

    public NotFoundPostListByTitle() { super("PostList를 찾을 수 있는 title이 존재하지 않습니다");}


    public NotFoundPostListByTitle(String message) {
        super(message);
    }
}
