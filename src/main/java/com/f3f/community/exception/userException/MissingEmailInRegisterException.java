package com.f3f.community.exception.userException;

public class MissingEmailInRegisterException extends IllegalArgumentException{
    public MissingEmailInRegisterException() {super("이메일 항목을 작성해주세요");}
}
