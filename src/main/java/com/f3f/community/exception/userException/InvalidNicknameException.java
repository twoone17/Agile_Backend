package com.f3f.community.exception.userException;

public class InvalidNicknameException extends IllegalArgumentException{
    public InvalidNicknameException() {super("유효하지 않은 닉네임입니다.");}
}
