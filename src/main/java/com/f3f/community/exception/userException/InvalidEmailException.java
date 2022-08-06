package com.f3f.community.exception.userException;

public class InvalidEmailException extends IllegalArgumentException{
    public InvalidEmailException() {super("유효하지 않은 이메일입니다.");}
}
