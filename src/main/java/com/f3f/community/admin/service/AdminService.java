package com.f3f.community.admin.service;

import com.f3f.community.exception.adminException.InvalidGradeException;
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
    public String banUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
        user.banUser();
        return "OK";
    }

    // id가 없다 == 이메일도 없다 ==> 불필요한 세분화 : 그냥 유저가 없다는 것만 체크해주자

    public String unbanUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
        user.unBanUser();
        return "OK";
    }

    // 수동으로 + 조건을 충족하면 자동으로 등업되는 로직도 생각해보기


    public String UpdateUserGrade(String email, String key) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
        if(!(key.equals("bronze") || key.equals("silver") || key.equals("gold") || key.equals("expert"))) {
            throw new InvalidGradeException();
        }
        user.updateUserGrade(key);
        return "OK";
    }

}
