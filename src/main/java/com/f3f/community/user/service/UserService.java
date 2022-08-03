package com.f3f.community.user.service;

import org.springframework.transaction.annotation.Transactional;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.exception.userException.*;
import org.springframework.stereotype.Service;
import com.f3f.community.user.domain.User;
import lombok.RequiredArgsConstructor;


import static com.f3f.community.user.dto.UserDto.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(User user) {
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

        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException());


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

        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException());

        if(beforePassword.equals(afterPassword)) {
            throw new DuplicateInChangePasswordException();
        }

        user.updatePassword(afterPassword);

        return "OK";
    }

    // findPassword, findEmail


    @Transactional
    public String delete(UserRequest userRequest) {

        //TODO: 로그인 여부(나중에), password 암호화
        // 이메일 검증, 본인의 이메일임을 검증해야함

        User user = userRepository.findByEmail(userRequest.getEmail()).orElseThrow(() -> new NotFoundUserException());

        if(!userRepository.existsByPassword(userRequest.getPassword())) {
            throw new InvalidPasswordException();
        }

        // 요청을 보낸 유저의 이메일과 패스워드가 데이터베이스 상에서도 서로 매핑이 되는지 확인한다.
        // 요청으로 들어온 패스워드와 DB에서 이메일로 받아온 유저 객체의 패스워드를 비교한다.
        if(!user.getPassword().equals(userRequest.getPassword())) {
            throw new NotMatchPasswordInDeleteUserException();
        }

        userRepository.deleteByEmail(userRequest.getEmail());
        return "OK";
    }

    //  유저 조회
    //  repository에서 제공하긴 하지만 에러 처리,


    //  외부로 나갈 기능들만 생각 x, 대부분의 기능은 내부에서 동작한다.

}
