package com.f3f.community.user.dto;

import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import lombok.*;
import org.aspectj.lang.annotation.Before;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
public class UserDto {

    // Inner classes

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class SaveRequest {
        @NotBlank
        private String email;

        @NotBlank
        // 최소 8자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$")
        private String password;

        @Pattern(regexp = "\\d{3}-\\d{3,4}-\\d{4}")
        private String phone;
        private UserGrade userGrade;

        // User
        @NotBlank
        @Size(min = 4, max = 10)
        private String nickname;
        private String address;


        public User toEntity() {
            return User.builder()
                    .email((this.email))
                    .password(this.password)
                    .nickname(this.nickname)
                    .userGrade(this.userGrade)
                    .phone(this.phone)
                    .address(this.address)
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    public static class ChangePasswordRequest {
        private String email;

        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$")
        private String AfterPassword;
        private String BeforePassword;

        @Builder
        public ChangePasswordRequest(String email,
                                     @NotBlank
                                     @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$")
                                     String ChangedPassword,
                                     String BeforePassword) {
            this.email = email;
            this.BeforePassword = BeforePassword;
            this.AfterPassword = ChangedPassword;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ChangeNicknameRequest {

        private String email;
        @NotBlank
        @Pattern(regexp = "^([a-zA-Z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣]).{1,10}$")
        private String AfterNickname;
        private String BeforeNickname;

        @Builder
        public ChangeNicknameRequest(String email,
                                     @NotBlank
                                     @Pattern(regexp = "^([a-zA-Z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣]).{1,10}$")
                                     String AfterNickname,
                                     String BeforeNickname) {
            this.email = email;
            this.BeforeNickname = BeforeNickname;
            this.AfterNickname = AfterNickname;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        private String email;
        private String password;
    }


}
