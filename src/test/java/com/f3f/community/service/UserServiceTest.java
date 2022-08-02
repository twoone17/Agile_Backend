package com.f3f.community.service;

import com.f3f.community.exception.userException.NoEmailAndPasswordException;
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

    private User createUser() {
        SaveRequest userInfo = new SaveRequest("temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, "james", "changwon", false);
        User user = userInfo.toEntity();
        return user;
    }

    @Test
    @DisplayName("유저서비스 가입 테스트")
    public void UserSaveTest() {
        // given
        User user = createUser();
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
                UserGrade.BRONZE, "james", "changwon", false);
        User user = userInfo.toEntity();

        // when
        Long joinId = userService.saveUser(user);
        Optional<User> byId = userRepository.findById(joinId);

        // then
        assertThat(byId.get().getId()).isEqualTo(joinId);
    }
    

    @Test
    @DisplayName("이메일 중복 검사 테스트")
    public void EmailDuplicationToFailTest() {
        // given
        // 이메일 중복 시나리오
        SaveRequest saveRequest1 = new SaveRequest("temp1@temp.com",
                "12345", "01012345678", UserGrade.BRONZE, "james", "changwon", false);
        SaveRequest saveRequest2 = new SaveRequest("temp1@temp.com",
                "1234567", "01012345678", UserGrade.BRONZE, "jack", "yatap", false);

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
                UserGrade.BRONZE, "cheolwoong", "changwon", false);
        SaveRequest saveRequest2 = new SaveRequest("temp312@temp.com", "1234567", "01012345678",
                UserGrade.BRONZE, "cheolwoong", "yatap", false);

        User NicknameTester1 = saveRequest1.toEntity();
        User NicknameTester2 = saveRequest2.toEntity();
        //when
        userService.saveUser(NicknameTester1);

        //then
        assertThrows(IllegalArgumentException.class,
                () -> userService.saveUser(NicknameTester2));
    }


    @Test
    @DisplayName("비밀번호 변경 - 변경 전 비밀번호와 일치")
    public void ChangePassword_DuplicationToFail()  {
        // given - 이메일이 일치하는 유저가 있어야 하므로 먼저 유저를 생성.
        SaveRequest saveRequest1 = new SaveRequest("oldstyle4@naver.com", "123456789asd", "01012345678",
                UserGrade.BRONZE, "CheolWoong", "changwon", false);
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
        assertThat(e.getMessage()).isEqualTo("비밀번호를 변경할 사용자가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("닉네임 변경 - 변경 전 닉네임과 일치")
    public void ChangeNickname_DuplicationToFail()  {
        // given - 이메일이 일치하는 유저가 있어야 하므로 먼저 유저를 생성.
        SaveRequest saveRequest1 = new SaveRequest("oldstyle4@naver.com", "123456789asd", "01012345678",
                UserGrade.BRONZE, "CheolWoong", "changwon", false);
        User user = saveRequest1.toEntity();
        userService.saveUser(user);

        // given - 그 후 위에서 생성한 유저의 이메일로 닉네임 변경을 요청하겠다.
        ChangeNicknameRequest changeNicknameRequest =
                new ChangeNicknameRequest("oldstyle4@naver.com", "CheolWoong", "CheolWoong");

        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.updateNickname(changeNicknameRequest));

        // then
        assertThat(e.getMessage()).isEqualTo("기존 닉네임과 변경 닉네임이 일치합니다.");
    }

    @Test
    @DisplayName("닉네임 변경 - 이메일 누락")
    public void ChangeNickname_MissingEmail() {
        //given
        ChangeNicknameRequest changeNicknameRequest =
                new ChangeNicknameRequest("emptyEmail", "james", "michael");

        //when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.updateNickname(changeNicknameRequest));

        //then
        assertThat(e.getMessage()).isEqualTo("닉네임을 변경할 사용자가 존재하지 않습니다.");
    }



    @Test
    @DisplayName("회원탈퇴 성공 테스트")
    public void deleteUserTest() {
        //given
        User user = createUser();

        //when
        userService.saveUser(user);
        String msg = userService.delete(user.getEmail(), user.getPassword());

        //then
        assertThat(msg).isEqualTo("OK");
    }

    @Test
    @DisplayName("회원탈퇴 실패 테스트 - 이메일 비밀번호 불일치")
    public void delete() {
        //given
        User user = createUser();

        //when
        userService.saveUser(user);
        NoEmailAndPasswordException e = assertThrows(NoEmailAndPasswordException.class,
                () -> userService.delete("oldstyle4@naver.com", "123456789a"));
        //then
        assertThat(e.getMessage()).isEqualTo("이메일이나 비밀번호가 일치하지 않습니다.");
    }

}