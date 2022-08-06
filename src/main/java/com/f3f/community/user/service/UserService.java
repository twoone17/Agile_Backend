package com.f3f.community.user.service;

import org.springframework.transaction.annotation.Transactional;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.exception.userException.*;
import org.springframework.stereotype.Service;
import com.f3f.community.user.domain.User;
import lombok.RequiredArgsConstructor;


import static com.f3f.community.user.dto.UserDto.*;

@Service
//@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(User user) {

        IsValidEmail(user.getEmail());
        IsValidPassword(user.getPassword());
        IsValidNickname(user.getNickname());

        if(userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException();
        }
        if(userRepository.existsByNickname(user.getNickname())) {
            throw new DuplicateNicknameException();
        }
        User saveUser = userRepository.save(user);
        return saveUser.getId();
    }

    @Transactional
    public String updateNickname(ChangeNicknameRequest changeNicknameRequest) {
        String email = changeNicknameRequest.getEmail();
        String beforeNickname = changeNicknameRequest.getBeforeNickname();
        String afterNickname = changeNicknameRequest.getAfterNickname();

        IsValidNickname(afterNickname);

        User user = FindUserByEmail(email);


        if(userRepository.existsByNickname(afterNickname)) {
            throw new DuplicateNicknameException();
        }

        if(beforeNickname.equals(afterNickname)) {
            throw new DuplicateNicknameException();
        }

        user.updateNickname(afterNickname);
        return "OK";
    }

    @Transactional
    public String updatePassword(ChangePasswordRequest changePasswordRequest) {
        String email = changePasswordRequest.getEmail();
        String beforePassword = changePasswordRequest.getBeforePassword();
        String afterPassword = changePasswordRequest.getAfterPassword();

        IsValidPassword(beforePassword);

        User user = FindUserByEmail(email);

        if(beforePassword.equals(afterPassword)) {
            throw new DuplicateInChangePasswordException();
        }

        user.updatePassword(afterPassword);

        return "OK";
    }

    //TODO 비밀번호 분실 시, 기존 비밀번호를 다시 알려줄지 초기화로 다시 설정하게 할지 고민하다
    //  두 기능 모두 구현해둠.

    public String changePasswordWithoutSignIn(ChangePasswordWithoutSignInRequest request) {
        String email = request.getEmail();
        String AfterPassword = request.getAfterPassword();
        IsValidPassword(AfterPassword);

        User user = FindUserByEmail(email);

        // TODO 이메일 인증
        CertificateEmail(user.getEmail());
        user.updatePassword(AfterPassword);
        return "OK";
    }

    public SearchedPassword findPassword(String email) {
        User user = FindUserByEmail(email);
        // TODO 이메일 인증
        CertificateEmail(user.getEmail());

        // TODO 암호화?
        String EncryptPW = user.getPassword();
        SearchedPassword searchedPassword = new SearchedPassword(EncryptPW);
        return searchedPassword;
    }



    @Transactional
    public String delete(UserRequest userRequest) {

        //TODO: 로그인 여부(나중에), password 암호화
        // 이메일 검증, 본인의 이메일임을 검증해야함

        User user = FindUserByEmail(userRequest.getEmail());

        if(!userRepository.existsByPassword(userRequest.getPassword())) {
            throw new NotFoundPasswordException();
        }

        if(!user.getPassword().equals(userRequest.getPassword())) {
            throw new NotMatchPasswordInDeleteUserException();
        }

        userRepository.deleteByEmail(userRequest.getEmail());
        return "OK";
    }


    public User FindUserByUserRequest(UserRequest userRequest) {
        if(!userRepository.existsByPassword(userRequest.getPassword())) {
            throw new NotFoundPasswordException();
        }
        User user = FindUserByEmail(userRequest.getEmail());
        return user;
    }

    public User FindUsersByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new NotFoundNicknameException());
        return user;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    // 공통화
    private User FindUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException());
        return user;
    }

    private boolean IsValidEmail(String email) {
        if(email.length() <= 0) {
            throw new InvalidEmailException();
        }
        return true;
    }

    private boolean IsValidPassword(String password) {
        if(password.length() <= 0) {
            throw new InvalidPasswordException();
        }
        return true;
    }

    private boolean IsValidNickname(String Nickname) {
        if(Nickname.length() <= 0) {
            throw new InvalidNicknameException();
        }
        return true;
    }

    private boolean CertificateEmail(String email) {
        // TODO 임시 조건.
        if(true) {

        } else {
            throw new CertificateEmailException();
        }
        return true;
    }

}
