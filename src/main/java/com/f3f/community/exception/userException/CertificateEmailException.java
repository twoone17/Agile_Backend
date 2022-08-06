package com.f3f.community.exception.userException;

public class CertificateEmailException extends IllegalArgumentException{
    public CertificateEmailException() {
        super("인증에 실패하였습니다.");
    }
}
