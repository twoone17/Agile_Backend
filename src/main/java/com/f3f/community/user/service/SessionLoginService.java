package com.f3f.community.user.service;

import com.f3f.community.configuration.EncryptionService;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class SessionLoginService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final EncryptionService encryptionService;


//    /**
//     * 아이디 비밀번호 일치 여부
//     * @param loginRequest
//     */
//    @Transactional(readOnly = true)
//    public void existByEmailAndPassword(UserDto.LoginRequest loginRequest) {
//    }
//    @Transactional(readOnly = true)
//    public void login(UserDto.LoginRequest request) {
//
//    }
//
//    public void setUserLevel(String email){
//    }
//
//    public void logout() {
//    }
//
//    public String getLoginUser() {
//    }
//
//
//    public UserDto.UserInfoDto getCurrentUser(String email){
//
//    }
//
//    private void banCheck(User user) {
//    }

}
