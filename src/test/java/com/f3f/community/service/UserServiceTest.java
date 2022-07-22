package com.f3f.community.service;

import com.f3f.community.exception.userException.NoEssentialFieldException;
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
        UserDto.SaveRequest userInfo = new UserDto.SaveRequest("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
        User user = userInfo.toEntity();
        // when
        Long joinId = userService.join(user);
        Optional<User> byId = userRepository.findById(joinId);
        // then
        assertThat(byId.get().getId()).isEqualTo(joinId);
    }

    @Test
    @DisplayName("Id로 유저 검색 테스트")
    public void findUserByIdTest() {
        // given
        UserDto.SaveRequest userInfo = new UserDto.SaveRequest("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
        User user = userInfo.toEntity();

        // when
        Long joinId = userService.join(user);
        Optional<User> byId = userRepository.findById(joinId);

        // then
        assertThat(byId.get().getId()).isEqualTo(joinId);
    }

    @Test
    @DisplayName("모든 유저 검색 테스트")
    public void findAllUsersTest() {
        // given
        long id = 1;
        UserDto.SaveRequest saveRequest1 = new UserDto.SaveRequest("temp1@temp.com",
                "12345", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
        UserDto.SaveRequest saveRequest2 = new UserDto.SaveRequest("temp2@temp.com",
                "1234567", "01012345678",
                UserGrade.BRONZE, "jack", "yatap");

        User user1 = saveRequest1.toEntity();
        User user2 = saveRequest2.toEntity();

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
        UserDto.SaveRequest saveRequest1 = new UserDto.SaveRequest("temp1@temp.com",
                "12345", "01012345678", UserGrade.BRONZE, "james", "changwon");
        UserDto.SaveRequest saveRequest2 = new UserDto.SaveRequest("temp1@temp.com",
                "1234567", "01012345678", UserGrade.BRONZE, "jack", "yatap");

        User EmailTester1 = saveRequest1.toEntity();
        User EmailTester2 = saveRequest2.toEntity();

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
        UserDto.SaveRequest saveRequest1 = new UserDto.SaveRequest("temp33@temp.com", "12345", "01012345678",
                UserGrade.BRONZE, "cheolwoong", "changwon");
        UserDto.SaveRequest saveRequest2 = new UserDto.SaveRequest("temp312@temp.com", "1234567", "01012345678",
                UserGrade.BRONZE, "cheolwoong", "yatap");

        User NicknameTester1 = saveRequest1.toEntity();
        User NicknameTester2 = saveRequest2.toEntity();
        //when
        userService.join(NicknameTester1);

        //then
        assertThrows(IllegalArgumentException.class,
                () -> userService.join(NicknameTester2));
    }
    
    @Test
    @DisplayName("닉네임 누락 테스트")
    public void NoNicknameTestToFail() {
        //given
        UserDto.SaveRequest saveRequest1 = new UserDto.SaveRequest("temp33@temp.com", "12345", "01012345678",
                UserGrade.BRONZE, "", "changwon");
        User NoNicknameUser = saveRequest1.toEntity();

        //when
        IllegalArgumentException e = assertThrows(NoEssentialFieldException.class,
                () -> userService.join(NoNicknameUser));

        //then
        assertThat(e.getMessage()).isEqualTo("닉네임 누락");
    }

    @Test
    @DisplayName("이메일 누락 테스트")
    public void NoEmailTestToFail() {
        //given
        UserDto.SaveRequest saveRequest1 = new UserDto.SaveRequest("", "12345", "01012345678",
                UserGrade.BRONZE, "CheolWoong", "changwon");
        User NoEmailUser = saveRequest1.toEntity();

        //when
        IllegalArgumentException e = assertThrows(NoEssentialFieldException.class,
                () -> userService.join(NoEmailUser));

        //then
        assertThat(e.getMessage()).isEqualTo("이메일 누락");
    }

    @Test
    @DisplayName("비밀번호 누락 테스트")
    public void NoPasswordTestToFail() {
        //given
        UserDto.SaveRequest saveRequest1 = new UserDto.SaveRequest("temmp@temp.com", "", "01012345678",
                UserGrade.BRONZE, "CheolWoong", "changwon");
        User NoPasswordUser = saveRequest1.toEntity();

        //when
        IllegalArgumentException e = assertThrows(NoEssentialFieldException.class,
                () -> userService.join(NoPasswordUser));

        //then
        assertThat(e.getMessage()).isEqualTo("비밀번호 누락");
    }

}