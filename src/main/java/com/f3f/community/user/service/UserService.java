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
        User saveUser = userRepository.save(user);
        return saveUser.getId();
    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public String updatePassword(ChangePasswordRequest changePasswordRequest) {
        String email = changePasswordRequest.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("사용자가 존재하지 않습니다."));

        if(changePasswordRequest.getBeforePassword().equals(changePasswordRequest.getAfterPassword()))
            throw new IllegalArgumentException("기존 비밀번호와 변경 비밀번호가 일치합니다.");

        user.updatePassword(changePasswordRequest.getAfterPassword());
        return "OK";
    }

    @Transactional
    public String delete(String email, String password) {

        if(!userRepository.existsByEmailAndPassword(email, password)) {
            throw new NoEmailAndPasswordException("이메일이나 비밀번호가 일치하지 않습니다.");
        }
        userRepository.deleteByEmail(email);
        return "OK";
    }



}
