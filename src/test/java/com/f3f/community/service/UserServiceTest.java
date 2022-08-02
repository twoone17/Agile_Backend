package com.f3f.community.service;

import com.f3f.community.exception.userException.*;
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
    @DisplayName("비밀번호 변경 - 변경 전 비밀번호와 일치")
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
    @DisplayName("비밀번호 변경 - 이메일 누락")
    public void ChangePassword_MissingEmail() {
        //given
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("12345789", "12345678abc@", "12345678a@");

        //when & then
        IllegalArgumentException e = assertThrows(NotFoundUserException.class, () -> userService.updatePassword(changePasswordRequest));

    }

    @Test
    @DisplayName("닉네임 변경 - 변경 전 닉네임과 일치")
    public void ChangeNickname_DuplicationToFail()  {
        // given - 이메일이 일치하는 유저가 있어야 하므로 먼저 유저를 생성.
        User user = createUser();
        userService.saveUser(user);

        // given - 그 후 위에서 생성한 유저의 이메일로 닉네임 변경을 요청하겠다.
        ChangeNicknameRequest changeNicknameRequest =
                new ChangeNicknameRequest(user.getEmail(), user.getNickname(), user.getNickname());

        // when & then
        IllegalArgumentException e = assertThrows(DuplicateInChangeNicknameException.class, () -> userService.updateNickname(changeNicknameRequest));
    }

    @Test
    @DisplayName("닉네임 변경 - 존재하지 않는 이메일")
    public void ChangeNickname_MissingEmail() {
        //given
        ChangeNicknameRequest changeNicknameRequest =
                new ChangeNicknameRequest("emptyEmail", "james", "michael");

        //when & then
        assertThrows(NotFoundUserException.class, () -> userService.updateNickname(changeNicknameRequest));
    }

    @Test
    @DisplayName("닉네임 변경 - 이미 존재하는 닉네임")
    public void ChangeNicknameToAlreadyExists() throws Exception {
        //given


        //when

        //then
    }


//    @Test
//    @DisplayName("회원탈퇴 성공 테스트")
//    public void deleteUserTest() {
//        //given
//        User user = createUser();
//
//        //when
//        userService.saveUser(user);
//        String msg = userService.delete(user.getEmail(), user.getPassword());
//
//        //then
//        assertThat(msg).isEqualTo("OK");
//    }

//    @Test
//    @DisplayName("회원탈퇴 실패 테스트 - 이메일 비밀번호 불일치")
//    public void delete() {
//        //given
//        User user = createUser();
//
//        //when
//        userService.saveUser(user);
//
//        //then
//
//    }

}