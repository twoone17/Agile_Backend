package com.f3f.community.user.service;

import org.springframework.transaction.annotation.Transactional;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.exception.userException.*;
import org.springframework.stereotype.Service;
import com.f3f.community.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;


import javax.validation.Valid;

import java.util.Optional;

import static com.f3f.community.user.dto.UserDto.*;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(@Valid SaveRequest saveRequest) {


        if(userRepository.existsByEmail(saveRequest.getEmail())) {
            throw new DuplicateEmailException();
        }
        if(userRepository.existsByNickname(saveRequest.getNickname())) {
            throw new DuplicateNicknameException();
        }

        User user = saveRequest.toEntity();
        User saveUser = userRepository.save(user);
        return saveUser.getId();
    }

    @Transactional
    public String updateNickname(ChangeNicknameRequest changeNicknameRequest) {
        String email = changeNicknameRequest.getEmail();
        String beforeNickname = changeNicknameRequest.getBeforeNickname();
        String afterNickname = changeNicknameRequest.getAfterNickname();


        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);


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


        User user = userRepository.findByEmail(changePasswordRequest.getEmail()).orElseThrow(NotFoundUserException::new);

        if(beforePassword.equals(afterPassword)) {
            throw new DuplicateInChangePasswordException();
        }

        user.updatePassword(afterPassword);

        return "OK";
    }

    //TODO 비밀번호 분실 시, 기존 비밀번호를 다시 알려줄지 초기화로 다시 설정하게 할지 고민하다
    //  두 기능 모두 구현해둠.

    @Transactional
    public String updatePasswordWithoutSignIn(ChangePasswordWithoutSignInRequest request) {
        String email = request.getEmail();
        String AfterPassword = request.getAfterPassword();

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(NotFoundUserException::new);

        // TODO 이메일 인증
        CertificateEmail(user.getEmail());
        user.updatePassword(AfterPassword);
        return "OK";
    }

    @Transactional
    public SearchedPassword findPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);
        // TODO 이메일 인증
        CertificateEmail(user.getEmail());

        // TODO 암호화?
        String EncryptPW = user.getPassword();
        SearchedPassword searchedPassword = new SearchedPassword(EncryptPW);
        return searchedPassword;
    }



    @Transactional
    public String delete(UserDeleteRequest userRequest) {

        //TODO: 로그인 여부(나중에), password 암호화
        // 이메일 검증, 본인의 이메일임을 검증해야함

        User user = userRepository.findByEmail(userRequest.getEmail()).orElseThrow(NotFoundUserException::new);

        if(!userRepository.existsByPassword(userRequest.getPassword())) {
            throw new NotFoundPasswordException();
        }

        if(!user.getPassword().equals(userRequest.getPassword())) {
            throw new NotMatchPasswordInDeleteUserException();
        }

        userRepository.deleteByEmail(userRequest.getEmail());
        return "OK";
    }


    @Transactional(readOnly = true)
    public User findUserByUserRequest(UserRequest userRequest) {
        if(!userRepository.existsByPassword(userRequest.getPassword())) {
            throw new NotFoundPasswordException();
        }
        User user = userRepository.findByEmail(userRequest.getEmail()).orElseThrow(NotFoundUserException::new);
        return user;
    }

    @Transactional(readOnly = true)

    public Long findUserByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(NotFoundNicknameException::new);
        return user.getId();
    }

    @Transactional(readOnly = true)
    public Long findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(NotFoundUserException::new);
        return user.getId();
    }

    @Transactional(readOnly = true)
    public Long findUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);
        return user.getId();
    }

    // id조회, 이메일 조회, 내부용
    // 유저 조회했을때 post를 다 보여줄거냐, 10개로 끊어서 보여줄지,
    // scrap도 마찬가지, user는 정보를 다 가지고 있어서 이걸 어떻게 뿌릴지 고민해야한다.


    private boolean CertificateEmail(String email) {
        // TODO 임시 조건.
        if(true) {

        } else {
            throw new CertificateEmailException();
        }
        return true;
    }

}
