package com.f3f.community.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
public class UserBase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserGrade userGrade;

    // id를 제외하고 객체를 만들기 위해서 생성자를 별도로 지정해줌.
    // @SuperBuilder를 사용하려 했으나 UserBase가 상속받는 BaseTimeEntity에 @SuperBuilder를
    // 추가할 경우 여러 문제가 발생하여 일단 보류해둔 상태
    public UserBase(String email, String password, String phone, UserGrade userGrade) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.userGrade = userGrade;
    }
}
