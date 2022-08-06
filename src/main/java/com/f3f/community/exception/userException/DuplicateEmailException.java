package com.f3f.community.exception.userException;

public class DuplicateEmailException extends IllegalArgumentException{
    public DuplicateEmailException() {super("이미 존재하는 이메일입니다.");}
}
