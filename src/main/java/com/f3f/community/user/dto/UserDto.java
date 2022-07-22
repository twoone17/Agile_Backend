package com.f3f.community.user.dto;

import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import lombok.*;

import javax.validation.constraints.NotBlank;
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
        @Size(min = 4, max = 15)
        private String password;

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


}
