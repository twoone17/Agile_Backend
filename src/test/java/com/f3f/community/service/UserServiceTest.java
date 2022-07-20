package com.f3f.community.service;

import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Test
    public void joinTest() {
        // given
        User user = new User("temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, "james", "changwon");

        // when
        Long joinId = userService.join(user);

        // then
        Assertions.assertThat(joinId).isEqualTo(user.getId());
    }

    @Test
    public void findTest() {
        // given
        User user = new User("temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, "james", "changwon");

        // when
        Long joinId = userService.join(user);
        Optional<User> findId = userService.findOne(joinId);

        // then
        Assertions.assertThat(findId.get().getId()).isEqualTo(joinId);
    }

    @Test
    public void findUsersTest() {
        // given
        long id = 1;
        User user1 = new User("temp1@temp.com", "12345", "01012345678", UserGrade.BRONZE, "james", "changwon");
        User user2 = new User("temp2@temp.com", "1234567", "01012345678", UserGrade.BRONZE, "jack", "yatap");

        //when
        userService.join(user1);
        userService.join(user2);

        //then
        Assertions.assertThat(userService.findUsers().size()).isEqualTo(2);
    }

}