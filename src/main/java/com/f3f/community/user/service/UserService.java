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
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이메일 중복");
        }
        // DB에 아직 들어가지 않았기 때문에 user.getId()의 값이 없을 것이다.
        // auto-increment이면 근데 중복검사를 굳이 할 필요가 있을까?
        // 일단은 제외하도록 하겠다.
        if(userRepository.existsByNickname(user.getNickname())) {
            throw new IllegalArgumentException("User 정보 중복");
        }
    }

    public Optional<User> findOne(Long id) {
        return userRepository.findById(id);
    }



    public List<User> findUsers() {
        return userRepository.findAll();
    }
}
