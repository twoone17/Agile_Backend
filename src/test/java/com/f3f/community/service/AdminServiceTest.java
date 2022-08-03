package com.f3f.community.service;

import com.f3f.community.admin.service.AdminService;
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
        Long savedId = userService.saveUser(user);

        //when
         Long bannedUserId = adminService.banUser(savedId);
        Optional<User> bannedUser = userRepository.findById(bannedUserId);

        //then
        assertThat(bannedUser.get().isBanned()).isEqualTo(true);
    }

    @Test
    @DisplayName("유저 차단 해제 테스트")
    public void unbanUserTest() {
        //given
        User user = createUser();
        Long savedId = userService.saveUser(user);
        Long bannedUserId = adminService.banUser(savedId);
        Optional<User> bannedUser = userRepository.findById(bannedUserId);

        //when
        Long unbannedUserId = adminService.unbanUser(bannedUser.get().getId());
        Optional<User> unbannedUser = userRepository.findById(unbannedUserId);

        //then
        assertThat(unbannedUser.get().isBanned()).isEqualTo(false);
    }

    @Test
    @DisplayName("존재하지 않는 유저 차단 - 차단 해제")
    public void NoUserToBanToFail() throws Exception {
        //given
        User user = createUser();

        //when
        Long EmptyId = userService.saveUser(user);

        //then
        assertThrows(NotFoundUserException.class,
                () -> adminService.banUser(1234L));
        assertThrows(NotFoundUserException.class,
                () -> adminService.unbanUser(1234L));
    }
}