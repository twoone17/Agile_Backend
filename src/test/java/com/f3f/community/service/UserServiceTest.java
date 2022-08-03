package com.f3f.community.service;

import com.f3f.community.exception.userException.*;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.h2.command.ddl.CreateUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
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
        SaveRequest userInfo = new SaveRequest("tempabc@tempabc.com", "ppadb123", "01098745632", UserGrade.BRONZE, "brandy", "pazu", false);
        User user = userInfo.toEntity();
        return user;
    }
    // 전달받은 매개변수를 유니크한 값으로 바꾼 user 엔티티를 저장한 뒤 반환한다.
    private User createUserWithParams(String key) {
        SaveRequest userInfo;
        switch (key) {
            case "email" :
                userInfo = new SaveRequest("UniqueEmail@naver.com", "123456", "01012345678", UserGrade.BRONZE, "james", "changwon", false);
                break;
            case "password" :
                userInfo = new SaveRequest("temp@temp.com", "UniquePassword", "01012345678", UserGrade.BRONZE, "james", "changwon", false);
                break;
            case "phone" :
                userInfo = new SaveRequest("temp@temp.com", "123456", "uniquePhone", UserGrade.BRONZE, "james", "changwon", false);
                break;
            case "nickname" :
                userInfo = new SaveRequest("temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, "UniqueNickname", "changwon", false);
                break;
            default:
                userInfo = new SaveRequest("temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, "james", "changwon", false);
                break;
        }
        User user = userInfo.toEntity();
        return user;
    }

    private User createUserWithUniqueCount(int i) {
        SaveRequest userInfo = new SaveRequest("tempabc@tempabc.com" + i, "ppadb123" + i, "0109874563" + i, UserGrade.BRONZE, "brandy" + i, "pazu", false);
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
    @DisplayName("회원가입 실패 - 이메일 누락")
    public void MissingEmailInRegisterToFail() {
        //given
        SaveRequest saveRequest = new SaveRequest("12356789", "1231",
                "01012345678", UserGrade.BRONZE, "james", "here", false);
        //when

        //then
    }

    @Test
    @DisplayName("이메일 중복 검사 테스트")
    public void EmailDuplicationToFailTest() {
        // given
        // 이메일 중복 시나리오
        User user1 = createUserWithParams("nickname");
        User user2 = createUserWithParams("phone");

        //when
        userService.saveUser(user1);

        //then
        assertThrows(DuplicateEmailException.class, () -> userService.saveUser(user2));
    }

    @Test
    @DisplayName("닉네임 중복 검사 테스트")
    public void NicknameDuplicationTestToFail() {
        //given
        User user1 = createUserWithParams("email");
        User user2 = createUserWithParams("phone");
        //when
        userService.saveUser(user1);

        //then
        assertThrows(DuplicateNicknameException.class, () -> userService.saveUser(user2));
    }


    @Test
    @DisplayName("비밀번호 변경 성공")
    public void ChangePasswordTest() {
        //given
        String newPW = "changed";
        User user = createUser();
        userService.saveUser(user);

        //when
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(user.getEmail(), newPW, user.getPassword());
        String result = userService.updatePassword(changePasswordRequest);

        //then
        assertThat(result).isEqualTo("OK");
        assertThat(user.getPassword()).isEqualTo(newPW);
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 변경 전 비밀번호와 일치")
    public void ChangePassword_DuplicationToFail()  {
        // given - 이메일이 일치하는 유저가 있어야 하므로 먼저 유저를 생성.
        User user = createUser();
        userService.saveUser(user);

        // given - 그 후 위에서 생성한 유저의 이메일로 비밀번호 변경을 요청하겠다.
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest(user.getEmail(), user.getPassword(), user.getPassword());

        // when & then
        IllegalArgumentException e = assertThrows(DuplicateInChangePasswordException.class, () -> userService.updatePassword(changePasswordRequest));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 이메일 누락")
    public void ChangePassword_MissingEmail() {
        //given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("12345789", "12345678abc@", "12345678a@");

        //when & then
        IllegalArgumentException e = assertThrows(NotFoundUserException.class, () -> userService.updatePassword(changePasswordRequest));

    }


    @Test
    @DisplayName("닉네임 변경 성공")
    public void ChangeNicknameTest() {
        //given
        String newNickname = "changed";
        User user = createUser();
        userService.saveUser(user);

        //when
        ChangeNicknameRequest changeNicknameRequest = new ChangeNicknameRequest(user.getEmail(), newNickname, user.getNickname());
        String result = userService.updateNickname(changeNicknameRequest);

        //then
        assertThat(result).isEqualTo("OK");
        assertThat(user.getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 변경 전 닉네임과 일치")
    public void ChangeNickname_DuplicationToFail()  {
        // given - 이메일이 일치하는 유저가 있어야 하므로 먼저 유저를 생성.
        User user = createUser();
        userService.saveUser(user);

        // given - 그 후 위에서 생성한 유저의 이메일로 닉네임 변경을 요청하겠다.
        ChangeNicknameRequest changeNicknameRequest = new ChangeNicknameRequest(user.getEmail(), user.getNickname(), user.getNickname());

        // when & then
        IllegalArgumentException e = assertThrows(DuplicateNicknameException.class, () -> userService.updateNickname(changeNicknameRequest));
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 존재하지 않는 이메일")
    public void ChangeNickname_MissingEmail() {
        //given
        ChangeNicknameRequest changeNicknameRequest = new ChangeNicknameRequest("emptyEmail", "james", "michael");

        //when & then
        assertThrows(NotFoundUserException.class, () -> userService.updateNickname(changeNicknameRequest));
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 이미 존재하는 닉네임")
    public void ChangeNicknameToAlreadyExists() {
        //given
        User user1 = createUserWithParams("email");
        User user2 = createUser();
        userService.saveUser(user1);
        userService.saveUser(user2);

        //when
        ChangeNicknameRequest changeNicknameRequest = new ChangeNicknameRequest(user1.getEmail(), user2.getNickname(), user1.getNickname());

        //then
        assertThrows(DuplicateNicknameException.class, () -> userService.updateNickname(changeNicknameRequest));
    }


    @Test
    @DisplayName("회원탈퇴 성공 테스트")
    public void deleteUserTest() {
       //given
        User user = createUser();
        userService.saveUser(user);

        //when
        UserRequest userRequest = new UserRequest(user.getEmail(), user.getPassword());
        String result = userService.delete(userRequest);

        //then
        assertThat(result).isEqualTo("OK");
        assertThat(userRepository.existsByEmail(userRequest.getEmail())).isEqualTo(false);
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 존재하지 않는 유저 이메일")
    public void deleteInvalidEmailUserToFail() {
        //given
        UserRequest userRequest = new UserRequest("invalidEmail", "tempPW");

        //when & then
        assertThrows(NotFoundUserException.class, () -> userService.delete(userRequest));
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 존재하지 않는 패스워드")
    public void deleteInvalidPasswordUserToFail() {
        //given
        User user = createUser();
        userService.saveUser(user);

        //when
        UserRequest userRequest = new UserRequest(user.getEmail(), "tempPW");

        //then
        assertThrows(InvalidPasswordException.class, () -> userService.delete(userRequest));
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 다른 유저의 패스워드")
    public void deleteOtherUserToFail() throws Exception {
        //given
        User user1 = createUser();
        User user2 = createUserWithUniqueCount(1);
        userService.saveUser(user1);
        // user2의 패스워드는 ppadb1231 이다.
        userService.saveUser(user2);

        //when
        // user1의 이메일, user2의 패스워드 모두 db에 존재하지만, 서로 매핑되지 않는 값이다.
        UserRequest userRequest = new UserRequest(user1.getEmail(), user2.getPassword());

        //then
        assertThrows(NotMatchPasswordInDeleteUserException.class, () -> userService.delete(userRequest));
    }


}