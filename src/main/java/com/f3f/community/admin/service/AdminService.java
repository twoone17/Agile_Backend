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
//    public String banUser(String email) {
//        // 거의 모든 Service logic에서 이메일을 체크하는데 공통으로 빼는게 맞을까?
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
//        user.banUser();
//        return "OK";
//    }
//
//
//    public String unbanUser(String email) {
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
//        user.unBanUser();
//        return "OK";
//    }

    // 수동으로 + 조건을 충족하면 자동으로 등업되는 로직도 생각해보기


    // enum의 key를 활용하자. 굳이 문자열로 넘기지 말고
    // 유저 등급이 올라가면 db 업데이트 체크
//    public String UpdateUserGrade(String email, int key) {
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
//        if(key < 0 || key >= 5) {
//            throw new InvalidGradeException();
//        }
//        user.updateUserGrade(key + 1);
//        return "OK";
//    }

}
