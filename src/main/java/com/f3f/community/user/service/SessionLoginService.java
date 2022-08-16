package com.f3f.community.user.service;


import com.f3f.community.common.configuration.EncryptionService;
import com.f3f.community.exception.userException.NotFoundPasswordException;
import com.f3f.community.exception.userException.NotFoundUserEmailException;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.dto.LoginRequestDto;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

import static com.f3f.community.common.constants.UserConstants.USER_ID;

//@Service
@RequiredArgsConstructor
public class SessionLoginService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final EncryptionService encryptionService;

    @Transactional(readOnly = true)
    public void login(LoginRequestDto loginRequest) {
        //ID,PASSWORD 검증
        existByEmailAndPassword(loginRequest);
        String email = loginRequest.getEmail();
        //userLevel SET 수정 필요함
        httpSession.setAttribute(USER_ID,email);
    }


    private boolean existByEmailAndPassword(LoginRequestDto loginRequest) {

        loginRequest.passwordEncryption(encryptionService);

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (!userRepository.existsByEmail(email)) {
            throw new NotFoundUserEmailException();
        }
        if (!userRepository.existsByPassword(password)) {
            throw new NotFoundPasswordException();
        }
        return true;
    }

    public void logOut(){
        httpSession.removeAttribute(USER_ID);
    }

    public String getLoginUsr(){
        return String.valueOf(httpSession.getAttribute(USER_ID));
    }

    public void setUserLevel(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 사용자입니다."));


        //httpSession.setAttribute(AUTH_State,);
    }
}
