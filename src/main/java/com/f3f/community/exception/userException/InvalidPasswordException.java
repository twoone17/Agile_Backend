package com.f3f.community.exception.userException;

public class InvalidPasswordException extends IllegalArgumentException{
    public InvalidPasswordException() {super("유효하지 않은 비밀번호입니다.");}
}
