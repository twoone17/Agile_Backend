package com.f3f.community.user.dto;

import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    // 제약조건은 임의로 걸어둠.

    // UserBase
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

    public Long toEntity() {
        User user = new User(email, password, phone, userGrade, nickname, address);
        return user.getId();
    }

//    public User toEntityWithPasswordEncode()

}
