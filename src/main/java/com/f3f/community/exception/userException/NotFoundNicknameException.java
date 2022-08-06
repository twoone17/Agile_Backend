package com.f3f.community.exception.userException;

public class NotFoundNicknameException extends IllegalArgumentException{
    public NotFoundNicknameException() {super("존재하지 않는 닉네임입니다.");}
}
