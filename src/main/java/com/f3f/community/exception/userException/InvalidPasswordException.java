package com.f3f.community.exception.userException;

public class InvalidPasswordException extends IllegalArgumentException{
    public InvalidPasswordException() {super("존재하지 않는 패스워드입니다.");}
}
