package com.f3f.community.admin.service;

import com.f3f.community.exception.adminException.BannedUserException;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.domain.UserLevel;
import com.f3f.community.user.dto.UserDto;
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



    // enum의 key를 활용하자. 굳이 문자열로 넘기지 말고
    // 유저 등급이 올라가면 db 업데이트 체크
    @Transactional
    public String updateUserGrade(@Valid UpdateGradeRequest updateGradeRequest) {
        String email = updateGradeRequest.getEmail();
        UserGrade userGrade = updateGradeRequest.getUserGrade();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
        if(user.getUserLevel() == UserLevel.BAN) {
            throw new BannedUserException();
        }
        user.updateUserGrade(userGrade);
        return resultString;
    }

    @Transactional
    public String updateUserGradeToExpert(@Valid  UpdateGradeToExpertRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
        if(user.getUserLevel() == UserLevel.BAN) {
            throw new BannedUserException();
        }
        user.updateUserGrade(UserGrade.EXPERT);
        return resultString;
    }

    @Transactional
    public String updateUserLevel(@Valid UpdateUserLevelRequest updateUserLevelRequest) {
        User user = userRepository.findByEmail(updateUserLevelRequest.getEmail()).orElseThrow(() -> new NotFoundUserException("해당 이메일의 유저가 없습니다."));
        UserLevel userLevel = updateUserLevelRequest.getUserLevel();
        user.updateUserLevel(userLevel);
        return resultString;
    }

}
