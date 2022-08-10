package com.f3f.community.admin.service;

import com.f3f.community.exception.adminException.InvalidGradeException;
import com.f3f.community.exception.adminException.InvalidUserLevelException;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import static com.f3f.community.user.dto.UserDto.*;

@Service
@Validated
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final String resultString = "OK";

//    public String banUser(@Valid  BanRequest banRequest) {
//        User user = userRepository.findByEmail(banRequest.getEmail()).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
//        // save banRequest.getContent()
//
//    }


    // enum의 key를 활용하자. 굳이 문자열로 넘기지 말고
    // 유저 등급이 올라가면 db 업데이트 체크
    @Transactional
    public String updateUserGrade(@Valid UpdateGradeRequest updateGradeRequest) {
        String email = updateGradeRequest.getEmail();
        int key = updateGradeRequest.getKey();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
        // TODO 밴 여부 확인?
        if(key < 0 || key >= 5) {
            throw new InvalidGradeException();
        }
        user.updateUserGrade(key + 1);
        return resultString;
    }

    @Transactional
    public String updateUserGradeToExpert(@Valid  UpdateGradeToExpertRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
        // TODO 밴 여부 확인?
        user.updateUserGrade(5);
        return resultString;
    }

    @Transactional
    public String banUser(@Valid BanRequest banRequest) {
        User user = userRepository.findByEmail(banRequest.getEmail()).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
        int key = banRequest.getKey();
        if(key < 0 || key >= 3) {
            throw new InvalidUserLevelException();
        }
        user.updateUserLevel(key);
        return resultString;
    }

}
