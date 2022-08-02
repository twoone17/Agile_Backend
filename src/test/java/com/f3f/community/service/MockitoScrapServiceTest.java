package com.f3f.community.service;

import com.f3f.community.exception.userException.EmailDuplicationException;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MockitoScrapServiceTest {

    @Mock
    ScrapRepository scrapRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    ScrapService scrapService;

    @InjectMocks
    UserService userService;



    private UserDto.SaveRequest createUserDto1(){
        return new UserDto.SaveRequest("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
    }

    private UserDto.SaveRequest createUserDto2(){
        return new UserDto.SaveRequest("temp2@temp2.com", "1234567", "01012341234",
                UserGrade.BRONZE, "own", "seoul");
    }
    private ScrapDto.SaveRequest createScrapDto1(User user) {

        return ScrapDto.SaveRequest.builder()
                .name("test")
                .postList(new ArrayList<>())
                .user(user)
                .build();
    }

    private ScrapDto.SaveRequest createScrapDto2(User user) {

        return ScrapDto.SaveRequest.builder()
                .name("test2")
                .postList(new ArrayList<>())
                .user(user)
                .build();
    }

    @AfterEach
    void clear(){
        userRepository.deleteAll();
        scrapRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("스크랩 생성 실패 테스트 - 유저 이메일이 이미 존재한다")
    public void createScrapTestToFail() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto1();
        ScrapDto.SaveRequest scrapDto = createScrapDto1(userDto.toEntity());

        // when
        when(userRepository.existsByEmail(any())).thenReturn(true);


        // then
        Assertions.assertThrows(EmailDuplicationException.class, () -> userService.saveUser(userDto.toEntity()));
        verify(userRepository, atLeastOnce()).existsByEmail("temp@temp.com");
    }
}
