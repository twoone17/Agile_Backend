package com.f3f.community.user.service;

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

    @Transactional  // 변경이 일어남
    public Long join(User user) {
        // 예외처리 해주기
        User saveUser;
        boolean dupUser = userRepository.existsById(user.getId());
        if(!dupUser){
           saveUser = userRepository.save(user);
        }else{
            throw new IllegalArgumentException();
        }
        return saveUser.getId();
    }

    public Optional<User> findOne(Long id) {
        return userRepository.findById(id);
    }

}
