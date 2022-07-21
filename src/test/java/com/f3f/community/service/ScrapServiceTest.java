package com.f3f.community.service;

import com.f3f.community.exception.scrapException.RebundantScrapNameException;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class ScrapServiceTest {

    @Autowired
    ScrapService service;

    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    PostRepository postRepository;


    @Test
    @Rollback
    public void scrapSaveTest() throws Exception{
        //given
        ScrapDto scrapDto = ScrapDto.builder()
                .id(1L)
                .name("test")
                .postList(null)
                .user(null)
                .build();


        // when
        service.createScrapCollection(scrapDto);
        // then
        Assertions.assertThat(true).isEqualTo(scrapRepository.existsByName("test"));
    }

    @Test
    @Rollback // 철웅이가 유저쪽 코드 빌더패턴으로 짜서 올려주면 여기 짤게영
    public void getScrapByUsernameTest() throws Exception{
        //given

        ScrapDto scrapDto1 = ScrapDto.builder().id(1L).build();

        // when

        // then
    }

    @Test
    @Rollback // 여기도 포스트 쪽 빌더패턴으로 코드 짜지면 테스트할게영
    public void savePostTest() throws Exception{
        //given


        // when

        // then
    }


    @Test
    @Rollback
    public void updateNameTest() throws Exception{
        //given
        ScrapDto scrapDto1 = ScrapDto.builder()
                .id(1L)
                .name("test").build();
        ScrapDto scrapDto2 = ScrapDto.builder()
                .id(2L)
                .name("test2").build();

        // when
        service.createScrapCollection(scrapDto1);
        service.createScrapCollection(scrapDto2);


        // then
        assertThrows(RebundantScrapNameException.class, () -> {
            service.updateCollectionName(2L, "test");
        });
    }
}