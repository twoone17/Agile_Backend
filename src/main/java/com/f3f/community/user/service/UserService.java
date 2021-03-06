package com.f3f.community.user.service;

import com.f3f.community.exception.userException.*;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static com.f3f.community.user.dto.UserDto.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new EmailDuplicationException();
        }
        if(userRepository.existsByNickname(user.getNickname())) {
            throw new NicknameDuplicationException();
        }
        if(user.getNickname().equals("")) {
            throw new NoEssentialFieldException("닉네임 누락");
        }
        if(user.getEmail().equals("")) {
            throw new NoEssentialFieldException("이메일 누락");
        }
        if(user.getPassword().equals("")) {
            throw new NoEssentialFieldException("비밀번호 누락");
        }
        User saveUser = userRepository.save(user);
        return saveUser.getId();
    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void updatePassword(ChangePasswordRequest changePasswordRequest) {
        String email = changePasswordRequest.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("사용자가 존재하지 않습니다."));

        if(changePasswordRequest.getBeforePassword().equals(changePasswordRequest.getAfterPassword()))
            throw new IllegalArgumentException("기존 비밀번호와 변경 비밀번호가 일치합니다.");

        user.updatePassword(changePasswordRequest.getAfterPassword());
    }

    @Transactional
    public void delete(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("사용자가 존재하지 않습니다."));

        if(!userRepository.existsByEmailAndPassword(email, password)) {
            throw new NoEmailAndPasswordException("이메일과 비밀번호가 없습니다.");
        }
        // 구현 예정

    }


}
