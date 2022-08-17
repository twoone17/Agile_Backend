package com.f3f.community.exception.commentException;

public class MaxDepthExeption extends IllegalArgumentException{
    public MaxDepthExeption() { super("depth를 초과했습니다."); }
}
