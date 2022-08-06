package com.f3f.community.service;

import com.f3f.community.admin.service.AdminService;
import com.f3f.community.exception.adminException.InvalidGradeException;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AdminService adminService;
    @Autowired
    UserService userService;

    private final String resultString = "OK";

    private User createUser() {
        UserDto.SaveRequest userInfo = new UserDto.SaveRequest("temp@temp.com", "123456", "01012345678", UserGrade.BRONZE, "james", "changwon", false);
        User user = userInfo.toEntity();
        return user;
    }

    @Test
    @DisplayName("유저 차단 테스트")
    public void banUserTest() {
        //given
        User user = createUser();
        userService.saveUser(user);

        //when
        Optional<User> bannedUser = userRepository.findByEmail(user.getEmail());

        //then
        assertThat(bannedUser.get().isBanned()).isEqualTo(true);
    }

    @Test
    @DisplayName("유저 차단 해제 테스트")
    public void unbanUserTest() {
        //given
        User user = createUser();
        userService.saveUser(user);
        adminService.banUser(user.getEmail());
        Optional<User> bannedUser = userRepository.findByEmail(user.getEmail());

        //when
        adminService.unbanUser(bannedUser.get().getEmail());
        Optional<User> unbannedUser = userRepository.findByEmail(user.getEmail());

        //then
        assertThat(unbannedUser.get().isBanned()).isEqualTo(false);
    }

    @Test
    @DisplayName("존재하지 않는 유저 차단 - 차단 해제")
    public void NoUserToBanToFail() throws Exception {
        //given
        User user = createUser();

        //when
        userService.saveUser(user);

        //then
        assertThrows(NotFoundUserException.class,
                () -> adminService.banUser("notFoundUser@temp.com"));
        assertThrows(NotFoundUserException.class,
                () -> adminService.unbanUser("notFoundUser2@temp.com"));
    }

//    @Test
//    @DisplayName("유저 등업 테스트")
//    public void UpdateUserGradeTest() {
//        //given
//        User user = createUser();
//        userService.saveUser(user);
//
//        //when
//        adminService.UpdateUserGrade(user.getEmail(), 4);
//
//        //then
//        assertThat(user.getUserGrade()).isEqualTo(UserGrade.EXPERT);
//    }
//
//    @Test
//    @DisplayName("유저 등업 실패 - 없는 유저, 없는 등급")
//    public void UpdateNotFoundUserGradeToFail() {
//        //given
//        User user = createUser();
//        userService.saveUser(user);
//        String notFoundEmail = "notFoundUser@user.com";
//        String notFoundGrade = "notFoundGrade";
//
//        //when & then
//        assertThrows(NotFoundUserException.class, () -> adminService.UpdateUserGrade(notFoundEmail, 2));
//        assertThrows(InvalidGradeException.class, () -> adminService.UpdateUserGrade(user.getEmail(), 37));
//    }
}