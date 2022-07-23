package com.f3f.community.service;

import com.f3f.community.exception.userException.NoEssentialFieldException;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.f3f.community.user.dto.UserDto.*;

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
        SaveRequest userInfo = new SaveRequest("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
        User user = userInfo.toEntity();
        // when
        Long joinId = userService.saveUser(user);
        Optional<User> byId = userRepository.findById(joinId);
        // then
        assertThat(byId.get().getId()).isEqualTo(joinId);
    }

    @Test
    @DisplayName("Id로 유저 검색 테스트")
    public void findUserByIdTest() {
        // given
        SaveRequest userInfo = new SaveRequest("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
        User user = userInfo.toEntity();

        // when
        Long joinId = userService.saveUser(user);
        Optional<User> byId = userRepository.findById(joinId);

        // then
        assertThat(byId.get().getId()).isEqualTo(joinId);
    }

    @Test
    @DisplayName("모든 유저 검색 테스트")
    public void findAllUsersTest() {
        // given
        SaveRequest saveRequest1 = new SaveRequest("temp1@temp.com",
                "12345", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
        SaveRequest saveRequest2 = new SaveRequest("temp2@temp.com",
                "1234567", "01012345678",
                UserGrade.BRONZE, "jack", "yatap");

        User user1 = saveRequest1.toEntity();
        User user2 = saveRequest2.toEntity();

        //when
        userService.saveUser(user1);
        userService.saveUser(user2);

        //then
        assertThat(userService.findUsers().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("이메일 중복 검사 테스트")
    public void EmailDuplicationToFailTest() {
        // given
        // 이메일 중복 시나리오
        SaveRequest saveRequest1 = new SaveRequest("temp1@temp.com",
                "12345", "01012345678", UserGrade.BRONZE, "james", "changwon");
        SaveRequest saveRequest2 = new SaveRequest("temp1@temp.com",
                "1234567", "01012345678", UserGrade.BRONZE, "jack", "yatap");

        User EmailTester1 = saveRequest1.toEntity();
        User EmailTester2 = saveRequest2.toEntity();

        //when
        userService.saveUser(EmailTester1);

        //then
        assertThrows(IllegalArgumentException.class,
                () -> userService.saveUser(EmailTester2));
    }

    @Test
    @DisplayName("닉네임 중복 검사 테스트")
    public void NicknameDuplicationTestToFail() {
        //given
        SaveRequest saveRequest1 = new SaveRequest("temp33@temp.com", "12345", "01012345678",
                UserGrade.BRONZE, "cheolwoong", "changwon");
        SaveRequest saveRequest2 = new SaveRequest("temp312@temp.com", "1234567", "01012345678",
                UserGrade.BRONZE, "cheolwoong", "yatap");

        User NicknameTester1 = saveRequest1.toEntity();
        User NicknameTester2 = saveRequest2.toEntity();
        //when
        userService.saveUser(NicknameTester1);

        //then
        assertThrows(IllegalArgumentException.class,
                () -> userService.saveUser(NicknameTester2));
    }
    
    @Test
    @DisplayName("닉네임 누락 테스트")
    public void MissingNicknameTestToFail() {
        //given
        SaveRequest saveRequest1 = new SaveRequest("temp33@temp.com", "12345", "01012345678",
                UserGrade.BRONZE, "", "changwon");
        User NoNicknameUser = saveRequest1.toEntity();

        //when
        IllegalArgumentException e = assertThrows(NoEssentialFieldException.class,
                () -> userService.saveUser(NoNicknameUser));

        //then
        assertThat(e.getMessage()).isEqualTo("닉네임 누락");
    }

    @Test
    @DisplayName("이메일 누락 테스트")
    public void MissingEmailTestToFail() {
        //given
        SaveRequest saveRequest1 = new SaveRequest("", "12345", "01012345678",
                UserGrade.BRONZE, "CheolWoong", "changwon");
        User NoEmailUser = saveRequest1.toEntity();

        //when
        IllegalArgumentException e = assertThrows(NoEssentialFieldException.class,
                () -> userService.saveUser(NoEmailUser));

        //then
        assertThat(e.getMessage()).isEqualTo("이메일 누락");
    }

    @Test
    @DisplayName("비밀번호 누락 테스트")
    public void MissingPasswordTestToFail() {
        //given
        SaveRequest saveRequest1 = new SaveRequest("temmp@temp.com", "", "01012345678",
                UserGrade.BRONZE, "CheolWoong", "changwon");
        User NoPasswordUser = saveRequest1.toEntity();

        //when
        IllegalArgumentException e = assertThrows(NoEssentialFieldException.class,
                () -> userService.saveUser(NoPasswordUser));

        //then
        assertThat(e.getMessage()).isEqualTo("비밀번호 누락");
    }

    @Test
    @DisplayName("비밀번호 변경 - 변경 전 비밀번호와 일치")
    public void ChangePassword_DuplicationToFail()  {
        // given - 이메일이 일치하는 유저가 있어야 하므로 먼저 유저를 생성.
        SaveRequest saveRequest1 = new SaveRequest("oldstyle4@naver.com", "123456789asd", "01012345678",
                UserGrade.BRONZE, "CheolWoong", "changwon");
        User user = saveRequest1.toEntity();
        userService.saveUser(user);

        // given - 그 후 위에서 생성한 유저의 이메일로 비밀번호 변경을 요청하겠다.
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("oldstyle4@naver.com", "12345678a@", "12345678a@");

        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.updatePassword(changePasswordRequest));

        // then
        assertThat(e.getMessage()).isEqualTo("기존 비밀번호와 변경 비밀번호가 일치합니다.");
    }

    @Test
    @DisplayName("비밀번호 변경 - 이메일 누락")
    public void ChangePassword_MissingEmail() {
        //given
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("12345789", "12345678abc@", "12345678a@");

        //when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.updatePassword(changePasswordRequest));

        //then
        assertThat(e.getMessage()).isEqualTo("사용자가 존재하지 않습니다.");
    }

}