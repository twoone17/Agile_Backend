package com.f3f.community.admin.service;

import com.f3f.community.exception.userException.NotFoundUserByIdException;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserService userService;

    public Long banUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundUserByIdException("해당 ID의 유저가 없습니다."));
        user.banUser();
//        return "OK";
        return user.getId();
    }

    public Long unbanUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundUserByIdException("해당 ID의 유저가 없습니다."));
        user.unBanUser();
//        return "OK";
        return user.getId();
    }
}
