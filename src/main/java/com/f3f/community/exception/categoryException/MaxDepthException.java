package com.f3f.community.exception.categoryException;

public class MaxDepthException extends IllegalArgumentException {
    public MaxDepthException() {
        super("최대 깊이를 도달하여 더 이상 카테고리를 생성할 수 없습니다");
    }

    public MaxDepthException(String s) {
        super(s);
    }
}
