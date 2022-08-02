package com.f3f.community.exception.userException;

public class DuplicateNicknameException extends IllegalArgumentException{
    public DuplicateNicknameException() {super("닉네임 중복입니다.");}
}
