package com.f3f.community.user.service;

import com.f3f.community.exception.userException.*;
import com.f3f.community.post.domain.Post;
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

    @Transactional
    public String updateNickname(ChangeNicknameRequest changeNicknameRequest) {
        String email = changeNicknameRequest.getEmail();
        String beforeNickname = changeNicknameRequest.getBeforeNickname();
        String afterNickname = changeNicknameRequest.getAfterNickname();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("닉네임을 변경할 사용자가 존재하지 않습니다."));

        if(beforeNickname.equals(afterNickname)) {
            throw new IllegalArgumentException("기존 닉네임과 변경 닉네임이 일치합니다.");
        }

        user.updateNickname(afterNickname);
        return "OK";
    }

    @Transactional
    public String updatePassword(ChangePasswordRequest changePasswordRequest) {
        String email = changePasswordRequest.getEmail();
        String beforePassword = changePasswordRequest.getBeforePassword();
        String afterPassword = changePasswordRequest.getAfterPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("비밀번호를 변경할 사용자가 존재하지 않습니다."));

        if(beforePassword.equals(afterPassword)) {
            throw new IllegalArgumentException("기존 비밀번호와 변경 비밀번호가 일치합니다.");
        }

        user.updatePassword(afterPassword);

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
