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
        validation(user);
        User saveUser = userRepository.save(user);
        return saveUser.getId();
    }

    // 중복 검증
    private void validation(User user) {
        if(userRepository.existsByNickname(user.getNickname())) {
            throw new IllegalArgumentException("유저 정보 중복");
        }
    }

    public Optional<User> findOne(Long id) {
        return userRepository.findById(id);
    }



    public List<User> findUsers() {
        return userRepository.findAll();
    }
}
