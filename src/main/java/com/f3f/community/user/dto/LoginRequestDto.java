package com.f3f.community.user.dto;

import com.f3f.community.common.configuration.EncryptionService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    private String email;
    private String password;

    public void passwordEncryption(EncryptionService encryptionService) {
        this.password = encryptionService.encrypt(password);
    }
}
