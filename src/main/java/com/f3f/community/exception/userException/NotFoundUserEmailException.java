package com.f3f.community.exception.userException;

public class NotFoundUserEmailException  extends IllegalArgumentException  {
    public NotFoundUserEmailException() {
        super("잘못된 이메일입니다.");
    }

    public NotFoundUserEmailException(String message) {
        super(message);
    }
}
