package com.f3f.community.service;

import com.f3f.community.exception.scrapException.NotFoundScrapNameException;
import com.f3f.community.exception.scrapException.NotFoundScrapPostListException;
import com.f3f.community.exception.scrapException.NotFoundScrapUserException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static com.f3f.community.scrap.dto.ScrapDto.*;
import static org.assertj.core.api.Assertions.*;
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

    @AfterEach
    void clear() {
        scrapRepository.deleteAll();
    }

    @Test
    @DisplayName("스크랩 저장 테스트")
    public void saveScrapTest() throws Exception{
        //given
        SaveRequest saveRequest = SaveRequest.builder()
//                .scrapId(1L)
                .name("test")
                .postList(null)
                .user(null)
                .build();

        Scrap newScrap = saveRequest.toEntity();
        // when
        System.out.println("scrap id " + newScrap.getScrapId());
        scrapRepository.save(newScrap);
        System.out.println("scrap id " + newScrap.getScrapId());

        // then
        assertThat(newScrap).isEqualTo(scrapRepository.findByScrapId(newScrap.getScrapId()));
        assertThat(newScrap).isEqualTo(scrapRepository.findByName(newScrap.getName()));

//        assertThat(newScrap).isEqualTo(scrapRepository.findByName(newScrap.getName()));
    }

    @Test
    @DisplayName("스크랩 아이디로 찾는 테스트")
    public void findScrapByIdTest() throws Exception{
        //given
        SaveRequest saveRequest = SaveRequest.builder()
                .name("test")
                .postList(null)
                .user(null)
                .build();
        Scrap newScrap = saveRequest.toEntity();

        // when
        scrapRepository.save(newScrap);
        // then
        assertThat(newScrap).isEqualTo(scrapRepository.findByScrapId(newScrap.getScrapId()));
        assertThat(newScrap).isNotEqualTo(scrapRepository.findByScrapId(newScrap.getScrapId() + 1));
    }

    @Test
    @DisplayName("스크랩 이름으로 찾는 테스트")
    public void findScrapByNameTest() throws Exception{
        //given
        SaveRequest saveRequest = SaveRequest.builder()
                .name("test")
                .postList(null)
                .user(null)
                .build();
        Scrap newScrap = saveRequest.toEntity();

        // when
        scrapRepository.save(newScrap);
        // then
        assertThat(newScrap).isEqualTo(scrapRepository.findByName(newScrap.getName()));
        assertThat(newScrap).isNotEqualTo(scrapRepository.findByName("test2"));
    }

    @Test
    @DisplayName("서비스 createScrapCollection 예외 발생 테스트 - 이름, 유저, 포스트리스트 검증")
    public void createScrapCollectionTestToFail() throws Exception{
        //given
        SaveRequest saveRequest1 = SaveRequest.builder()
                .name("test")
                .postList(new ArrayList<Post>())
                .build();
        SaveRequest saveRequest2 = SaveRequest.builder()
                .postList(new ArrayList<Post>())
                .user(new User())
                .build();
        SaveRequest saveRequest3 = SaveRequest.builder()
                .name("test")
                .user(new User())
                .postList(null)
                .build();

        // when
        assertThrows(NotFoundScrapUserException.class, ()->{
            service.createScrapCollection(saveRequest1);
        });
        assertThrows(NotFoundScrapNameException.class, () -> {
            service.createScrapCollection(saveRequest2);
        });
        assertThrows(NotFoundScrapPostListException.class, () -> {
            service.createScrapCollection(saveRequest3);
        });
        // then

    }
    
    @Test
    @DisplayName("서비스 createScrapCollection 테스트 - 유저 저장, 중복 유저 저장 불가") // 유저 쪽 구현되면 추가 예정
    public void createScrapCollectionTest() throws Exception{
        //given
        
        
        // when
        
        // then
    }


    @Test // 철웅이가 유저쪽 코드 빌더패턴으로 짜서 올려주면 여기 짤게영
    public void findScrapByUserTest() throws Exception{
        //given


        // when

        // then
    }






    @Test // 여기도 포스트 쪽 빌더패턴으로 코드 짜지면 테스트할게영
    public void savePostTest() throws Exception{
        //given


        // when

        // then
    }


    @Test // 여기도 추후에 추가할게영, 유저 쪽에서 스크랩 리스트리턴해주는 코드 추가된 후에 진행
    @DisplayName("스크랩 컬렉션 이름 변경 테스트")
    public void updateNameTest() throws Exception{
        //given


        // when



        // then

    }

    @Test
    @DisplayName("스크랩 리포지토리에서 스크랩 삭제 테스트")
    public void deleteScrapTest() throws Exception{
        //given
        SaveRequest saveRequest = SaveRequest.builder()
                .name("test")
                .postList(null)
                .user(null)
                .build();
        Scrap newScrap = saveRequest.toEntity();

        // when
        scrapRepository.save(newScrap);
        assertThat(newScrap).isEqualTo(scrapRepository.findByScrapId(newScrap.getScrapId()));
        // then
        scrapRepository.delete(newScrap);
        assertThat(false).isEqualTo(scrapRepository.existsByScrapId(newScrap.getScrapId()));
        assertThat(false).isEqualTo(scrapRepository.existsByName(newScrap.getName()));
    }



}