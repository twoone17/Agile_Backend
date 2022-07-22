package com.f3f.community.service;

import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("유저서비스 가입 테스트")
    public void UserSaveTest() {
        // given
        UserDto userDto = new UserDto("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
        User user = userDto.toEntity();
        // when
        Long joinId = userService.join(user);
        Optional<User> joinOne = userService.findOne(joinId);
        // then
        assertThat(joinOne.get().getId()).isEqualTo(joinId);
    }

    @Test
    @DisplayName("Id로 유저 검색 테스트")
    public void findUserByIdTest() {
        // given
        UserDto userDto = new UserDto("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
        User user = userDto.toEntity();

        // when
        Long joinId = userService.join(user);
        Optional<User> findId = userService.findOne(joinId);

        // then
        assertThat(findId.get().getId()).isEqualTo(joinId);
    }

    @Test
    @DisplayName("모든 유저 검색 테스트")
    public void findAllUsersTest() {
        // given
        long id = 1;
        UserDto userDto1 = new UserDto("temp1@temp.com", "12345", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
        UserDto userDto2 = new UserDto("temp2@temp.com", "1234567", "01012345678",
                UserGrade.BRONZE, "jack", "yatap");

        User user1 = userDto1.toEntity();
        User user2 = userDto2.toEntity();

        //when
        userService.join(user1);
        userService.join(user2);

        //then
        assertThat(userService.findUsers().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("이메일 중복 검사 테스트")
    public void EmailDuplicationToFailTest() {
        // given
        // 이메일 중복 시나리오
        UserDto EmailTesterDto1 = new UserDto("temp1@temp.com", "12345", "01012345678", UserGrade.BRONZE, "james", "changwon");
        UserDto EmailTesterDto2 = new UserDto("temp1@temp.com", "1234567", "01012345678", UserGrade.BRONZE, "jack", "yatap");

        User EmailTester1 = EmailTesterDto1.toEntity();
        User EmailTester2 = EmailTesterDto2.toEntity();

        //when
        userService.join(EmailTester1);

        //then
        assertThrows(IllegalArgumentException.class,
                () -> userService.join(EmailTester2));
    }

    @Test
    @DisplayName("닉네임 중복 검사 테스트")
    public void NicknameDuplicationTestToFail() {
        //given
        UserDto NicknameTesterDto1 = new UserDto("temp33@temp.com", "12345", "01012345678",
                UserGrade.BRONZE, "cheolwoong", "changwon");
        UserDto NicknameTesterDto2 = new UserDto("temp312@temp.com", "1234567", "01012345678",
                UserGrade.BRONZE, "cheolwoong", "yatap");

        User NicknameTester1 = NicknameTesterDto1.toEntity();
        User NicknameTester2 = NicknameTesterDto2.toEntity();
        //when
        userService.join(NicknameTester1);

        //then
        assertThrows(IllegalArgumentException.class,
                () -> userService.join(NicknameTester2));
    }

}