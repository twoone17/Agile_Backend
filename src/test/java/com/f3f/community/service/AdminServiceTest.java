package com.f3f.community.service;

import com.f3f.community.admin.service.AdminService;
import com.f3f.community.exception.adminException.InvalidGradeException;
import com.f3f.community.exception.adminException.InvalidUserLevelException;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.domain.UserLevel;
import com.f3f.community.user.domain.UserLogin;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static com.f3f.community.user.dto.UserDto.*;

@SpringBootTest
class AdminServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AdminService adminService;
    @Autowired
    UserService userService;

    private final String resultString = "OK";

    @AfterEach
    public void delete() {
        userRepository.deleteAll();
    }

    private SaveRequest createUser() {
        SaveRequest userInfo = new SaveRequest("temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH,"james", "changwon");
//        User user = userInfo.toEntity();
        return userInfo;
    }

    @Test
    @DisplayName("유저 차단 테스트 성공")
    public void banUserTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);
        BanRequest banRequest = new BanRequest(user.get().getEmail(),2, "욕설");

        //when
        adminService.banUser(banRequest);
        Optional<User> bannedUser = userRepository.findByEmail(user.get().getEmail());

        //then
        assertThat(bannedUser.get().getUserLevel()).isEqualTo(UserLevel.BAN);
    }

    @Test
    @DisplayName("유저 차단 테스트 실패 - 존재하지 않는 이메일")
    public void banUnknownEmailUserToFail() {
        //given
        String unknownEmail = "unKnownEmail@email.com";
        BanRequest banRequest = new BanRequest(unknownEmail, 1, "욕설");

        //when & then
        assertThrows(NotFoundUserException.class, () -> adminService.banUser(banRequest));
    }

    @Test
    @DisplayName("유저 차단 테스트 실패 - 존재하지 않는 UserLevel")
    public void updateUserToInvalidUserLevelToFail() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);
        int invalidKey = 37;
        BanRequest banRequest = new BanRequest(user.get().getEmail(),invalidKey, "욕설");

        //when & then
        assertThrows(InvalidUserLevelException.class, () -> adminService.banUser(banRequest));
    }

    @Test
    @DisplayName("유저 차단 테스트 실패 - 올바르지 않은 이메일 형식")
    public void banUserWithInvalidEmailToFail() {
        //given
        BanRequest banRequest = new BanRequest("", 2, "욕설");

        //when & then
        assertThrows(ConstraintViolationException.class, () -> adminService.banUser(banRequest));
    }

//    @Test
//    @DisplayName("유저 차단 해제 테스트 성공")
//    public void unbanUserTest() {
//        //given
//        SaveRequest userDTO = createUser();
//        Long aLong = userService.saveUser(userDTO);
//        Optional<User> user = userRepository.findById(aLong);
//        BanRequest banRequest = new BanRequest(user.get().getEmail(),2, "욕설");
//
//        //when
//        adminService.banUser(banRequest);
//        Optional<User> bannedUser = userRepository.findByEmail(user.get().getEmail());
//
//        //then
//    }

    @Test
    @DisplayName("유저 등업 테스트")
    public void updateUserGradeTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);
        UpdateGradeRequest updateGradeRequest = new UpdateGradeRequest(user.get().getEmail(), 3);

        //when
        adminService.updateUserGrade(updateGradeRequest);
        Optional<User> user2 = userRepository.findByEmail(user.get().getEmail());
        //then
        assertThat(user2.get().getUserGrade()).isEqualTo(UserGrade.PLATINUM);
    }

    @Test
    @DisplayName("유저 등업 실패 - 없는 유저")
    public void updateNotFoundUserToFail() {
        //given
        String notFoundEmail = "notFoundUser@user.com";
        UpdateGradeRequest updateGradeRequest = new UpdateGradeRequest(notFoundEmail, 3);
        //when & then
        assertThrows(NotFoundUserException.class, () -> adminService.updateUserGrade(updateGradeRequest));
    }

    @Test
    @DisplayName("유저 등업 실패 - 유효하지 않은 등급")
    public void updateNotFoundUserGradeToFail() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);
        int notFoundKey = 37;
        UpdateGradeRequest updateGradeRequest = new UpdateGradeRequest(user.get().getEmail(), notFoundKey);

        //when & then
        assertThrows(InvalidGradeException.class, () -> adminService.updateUserGrade(updateGradeRequest));
    }

    @Test
    @DisplayName("전문가 유저로 등업")
    public void updateUserGradeToExpert() {
        //given
        SaveRequest userDTO = createUser();
        userService.saveUser(userDTO);

        UpdateGradeToExpertRequest request = new UpdateGradeToExpertRequest(userDTO.getEmail(), "주식");

        //when
        adminService.updateUserGradeToExpert(request);
        Long aLong = userService.findUserByEmail(request.getEmail());
        Optional<User> expertUser = userRepository.findById(aLong);

        //then
//        assertThat(expertUser.get().getUserGrade()).isEqualTo(UserGrade.EXPERT);
        assertThat(expertUser.get().getUserGrade()).isEqualTo(UserGrade.EXPERT);
    }
}