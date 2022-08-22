package com.f3f.community.user.dto;

import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.domain.UserLevel;
import com.f3f.community.user.domain.UserLogin;
import lombok.*;
import org.aspectj.lang.annotation.Before;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;

// 패스워드 정규식은 프론트관할, 일단은 길이 체크만

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
        private String password;

        private String phone;
        private UserGrade userGrade;
        private UserLevel userLevel;

        // User
        @NotBlank
        private String nickname;
        private String address;


        public User toEntity() {
            return User.builder()
                    .email((this.email))
                    .password(this.password)
                    .nickname(this.nickname)
                    .userGrade(this.userGrade)
                    .userLevel(this.userLevel)
                    .phone(this.phone)
                    .address(this.address)
                    .scraps(new ArrayList<>())
                    .likes(new ArrayList<>())
                    .posts(new ArrayList<>())
                    .comments(new ArrayList<>())
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    public static class ChangePasswordRequest {
        @NotBlank
        private String email;

        @NotBlank
        private String AfterPassword;

        @NotBlank
        private String BeforePassword;

        @Builder
        public ChangePasswordRequest(String email,
                                     @NotBlank
                                     String ChangedPassword,
                                     @NotBlank
                                     String BeforePassword) {
            this.email = email;
            this.BeforePassword = BeforePassword;
            this.AfterPassword = ChangedPassword;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public  static class ChangePasswordWithoutSignInRequest {

        @NotBlank
        private String email;

        @NotBlank
        private String AfterPassword;
    }


    @Getter
    @NoArgsConstructor
    public static class ChangeNicknameRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String AfterNickname;
        private String BeforeNickname;

        @Builder
        public ChangeNicknameRequest(String email,
                                     @NotBlank
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
    public static class UserRequest {
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDeleteRequest {
        @NotBlank
        private String email;

        @NotBlank
        private String password;
        // UserLogin이 존재했을때 여기서 로그인 여부도 값으로 전달 받았었다.
    }



    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchedPassword {
        // TODO 암호화?
        private String password;
    }

    // 자주 사용되지 않는 클래스는 static 말고 그냥 이너 클래스로 쓰면 더 좋다.

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateUserLevelRequest {
        @NotBlank
        private String email;
        private UserLevel userLevel;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateGradeRequest {
        @NotBlank
        private String email;
        @NotNull
        private UserGrade userGrade;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateGradeToExpertRequest {
        @NotBlank
        private String email;
        private String section;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyPageRequest {
        @NotBlank
        private String email;
        private int limit;
        private String option;
    }
}