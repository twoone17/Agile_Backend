package com.f3f.community.service;

import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    long id = 1;

    @Test
    public void joinTest() {
        // given
        User user = new User(id++, "temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, "james", "changwon",
                null, null, null, null);

        // when
        Long joinId = userService.join(user);

        // then
        Assertions.assertThat(joinId).isEqualTo(user.getId());
    }

    @Test
    public void findTest() {
        // given
        User user = new User(id++, "temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, "james", "changwon",
                null, null, null, null);

        // when
        Long joinId = userService.join(user);
        User oneNickname = userService.findOneNickname(user);

        // then
        Assertions.assertThat(oneNickname.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    public void findUsersTest() {
        // given
        User user1 = new User(id++, "temp1@temp.com", "12345", "01012345678", UserGrade.BRONZE, "james", "changwon",
                null, null, null, null);
        User user2 = new User(id++, "temp2@temp.com", "1234567", "01012345678", UserGrade.BRONZE, "jack", "yatap",
                null, null, null, null);

        //when
        userService.join(user1);
        userService.join(user2);

        //then
        Assertions.assertThat(userService.findUsers().size()).isEqualTo(2);
    }

}