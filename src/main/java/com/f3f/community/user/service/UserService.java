package com.f3f.community.user.service;

import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserBase;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long join(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이메일 중복");
        }
        if(userRepository.existsByNickname(user.getNickname())) {
            throw new IllegalArgumentException("User 정보 중복");
        }
        if(user.getNickname().equals("")) {
            throw new IllegalArgumentException("닉네임 누락");
        }
        if(user.getEmail().equals("")) {

        }
        User saveUser = userRepository.save(user);
        return saveUser.getId();
    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }


}
