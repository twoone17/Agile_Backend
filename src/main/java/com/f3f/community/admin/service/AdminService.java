package com.f3f.community.admin.service;

import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserService userService;

    public String banUser(Long id) {
        Optional<User> byId = userRepository.findById(id);
        return "구현중";
    }
}
