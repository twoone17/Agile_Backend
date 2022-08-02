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
            throw new DuplicateInChangeNicknameException();
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
    public String delete(String email, String password) {

        // delete에 들어가기엔 위험요소가 있음 - DB에 존재여부만 보고 삭제한다는게 말이 안된다고 함
        //
        //if(!userRepository.existsByEmailAndPassword(email, password)) {
        //    throw new NoEmailAndPasswordException("이메일이나 비밀번호가 일치하지 않습니다.");
        //}

        // 로그인 여부(나중에), password 암호화
        // 이메일 검증, 본인의 이메일임을 검증해야함
        // 패스워드도 DB에 존재하는지 검사하고,



        userRepository.deleteByEmail(email);
        return "OK";
    }

    //  유저 조회
    //  repository에서 제공하긴 하지만 에러 처리,
    //  조회 관련 추가

    //  외부로 나갈 기능들만 생각 x, 대부분의 기능은 내부에서 동작한다.

}
