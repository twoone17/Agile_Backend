package com.f3f.community.exception.userException;

public class DuplicateChangeInfoException extends IllegalArgumentException{
    public DuplicateChangeInfoException() {super("변경하려는 정보와 기존 정보가 일치합니다.");}
}
