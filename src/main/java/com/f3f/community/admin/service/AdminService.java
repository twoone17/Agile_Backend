package com.f3f.community.admin.service;

import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    // 이메일로 수정하기
    public Long banUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundUserException("해당 ID의 유저가 없습니다."));
        user.banUser();
//        return "OK";
        return user.getId();
    }

    // id가 없다 == 이메일도 없다 ==> 불필요한 세분화 : 그냥 유저가 없다는 것만 체크해주자

    public Long unbanUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundUserException("해당 ID의 유저가 없습니다."));
        user.unBanUser();
//        return "OK";
        return user.getId();
    }

    // 수동으로 + 조건을 충족하면 자동으로 등업되는 로직도 생각해보기

//    public Long UpdateUserGrade(Long id) {
//        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundUserByIdException("해당 ID의 유저가 없습니다."));
//    }

}
