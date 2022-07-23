package com.f3f.community.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    protected Long id;

    @Column(name = "user_email")
    protected String email;

    @Column(name = "user_password")
    protected String password;

    @Column(name = "user_phone")
    protected String phone;

    @Enumerated(EnumType.STRING)
    protected UserGrade userGrade;


}
