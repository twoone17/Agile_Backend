package com.f3f.community.exception.adminException;

public class InvalidGradeException extends IllegalArgumentException{
    public InvalidGradeException() {super("유효하지 않은 유저 등급입니다.");}
}
