package com.f3f.community.service;

import com.f3f.community.admin.service.AdminService;
import com.f3f.community.exception.adminException.InvalidGradeException;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        SaveRequest userInfo = new SaveRequest("temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, "james", "changwon");
//        User user = userInfo.toEntity();
        return userInfo;
    }

//    @Test
//    @DisplayName("유저 차단 테스트")
//    public void banUserTest() {
//        //given
//        UserDto.SaveRequest userDTO = createUser();
//        Long aLong = userService.saveUser(userDTO);
//        Optional<User> byId = userRepository.findById(aLong);
//
//        //when
//        Optional<User> bannedUser = userRepository.findByEmail(byId.get().getEmail());
//
//        //then
//        assertThat(bannedUser.get().isBanned()).isEqualTo(true);
//    }
//
//    @Test
//    @DisplayName("유저 차단 해제 테스트")
//    public void unbanUserTest() {
//        //given
//        UserDto.SaveRequest userDTO = createUser();
//        Long aLong = userService.saveUser(userDTO);
//        Optional<User> user = userRepository.findById(aLong);
//        adminService.banUser(user.get().getEmail());
//        Optional<User> bannedUser = userRepository.findByEmail(user.get().getEmail());
//
//        //when
//        adminService.unbanUser(bannedUser.get().getEmail());
//        Optional<User> unbannedUser = userRepository.findByEmail(user.get().getEmail());
//
//        //then
//        assertThat(unbannedUser.get().isBanned()).isEqualTo(false);
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 유저 차단 - 차단 해제")
//    public void NoUserToBanToFail() throws Exception {
//        //given
//        UserDto.SaveRequest userDTO = createUser();
//
//        //when
//        Long aLong = userService.saveUser(userDTO);
//        Optional<User> user = userRepository.findById(aLong);
//
//        //then
//        assertThrows(NotFoundUserException.class,
//                () -> adminService.banUser("notFoundUser@temp.com"));
//        assertThrows(NotFoundUserException.class,
//                () -> adminService.unbanUser("notFoundUser2@temp.com"));
//    }

    @Test
    @DisplayName("유저 등업 테스트")
    public void updateUserGradeTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        adminService.updateUserGrade(user.get().getEmail(), 3);
        Optional<User> user2 = userRepository.findByEmail(user.get().getEmail());
        //then
        assertThat(user2.get().getUserGrade()).isEqualTo(UserGrade.PLATINUM);
    }

    @Test
    @DisplayName("유저 등업 실패 - 없는 유저")
    public void updateNotFoundUserToFail() {
        //given
        String notFoundEmail = "notFoundUser@user.com";

        //when & then
        assertThrows(NotFoundUserException.class, () -> adminService.updateUserGrade(notFoundEmail, 2));
    }

    @Test
    @DisplayName("유저 등업 실패 - 유효하지 않은 등급")
    public void updateNotFoundUserGradeToFail() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        int notFoundKey = 37;

        //when & then
        assertThrows(InvalidGradeException.class, () -> adminService.updateUserGrade(user.get().getEmail(), notFoundKey));
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