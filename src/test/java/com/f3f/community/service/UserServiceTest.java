package com.f3f.community.service;

import com.f3f.community.user.domain.User;
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

    @Test
    public void joinTest() {
        // given
        User user = new User("james", "changwon", null, null, null);

        // when
        Long joinId = userService.join(user);

        // then
        Assertions.assertThat(joinId).isEqualTo(user.getId());
    }


}