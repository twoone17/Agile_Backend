package com.f3f.community.exception.userException;

public class NoEmailAndPasswordException extends IllegalArgumentException{
    public NoEmailAndPasswordException(String message) {
        super(message);
    }

}
