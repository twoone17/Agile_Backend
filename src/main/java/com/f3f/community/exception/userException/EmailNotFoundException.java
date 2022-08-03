package com.f3f.community.exception.userException;

public class EmailNotFoundException extends IllegalArgumentException{

    public EmailNotFoundException(String message) {
        super(message);
    }

}
