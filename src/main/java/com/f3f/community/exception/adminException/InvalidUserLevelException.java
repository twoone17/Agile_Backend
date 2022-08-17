package com.f3f.community.exception.adminException;

public class InvalidUserLevelException extends IllegalArgumentException{
    public InvalidUserLevelException() {super("유효하지 않은 UserLevel 입니다.");}
}
