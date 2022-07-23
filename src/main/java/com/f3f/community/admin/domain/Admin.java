package com.f3f.community.admin.domain;

import com.f3f.community.user.domain.UserBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
// constructor Admin() is already defined in class Admin 오류 발생으로 임시 주석처리 하겠음. - 철웅
//@AllArgsConstructor
public class Admin extends UserBase {

}
