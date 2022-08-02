package com.f3f.community.service;

import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.scrapException.*;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.post.domain.ScrapPost;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.repository.ScrapPostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
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
    ScrapService scrapService;

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScrapPostRepository scrapPostRepository;

    private UserDto.SaveRequest createUserDto1(){
        return new UserDto.SaveRequest("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon", false);
    }

    private UserDto.SaveRequest createUserDto2(){
        return new UserDto.SaveRequest("temp2@temp2.com", "1234567", "01012341234",
                UserGrade.BRONZE, "own", "seoul", false);
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

    private PostDto.SaveRequest createPostDto1(User user) {
        return PostDto.SaveRequest.builder()
                .title("test title")
                .content("test content for test")
                .author(user)
                .scrapList(new ArrayList<>())
                .build();
    }

    private PostDto.SaveRequest createPostDto2(User user) {
        return PostDto.SaveRequest.builder()
                .title("test title2")
                .content("test content for test2")
                .author(user)
                .scrapList(new ArrayList<>())
                .build();
    }



    // createScrap 테스트 시작
    @Test
    @DisplayName("서비스 createScrapCollection 예외 발생 테스트 - 유저가 없어서 생성안된다")
    public void createScrapTestToFailByNullUser() throws Exception{
        //given
        ScrapDto.SaveRequest saveRequest1 = SaveRequest.builder()
                .name("test")
                .postList(new ArrayList<ScrapPost>())
                .build();


        // then
        assertThrows(NotFoundScrapUserException.class, ()-> scrapService.createScrap(saveRequest1));

    }

    @Test
    @DisplayName("서비스 createScrapCollection 예외 발생 테스트 - 이름이 없어서 생성안된다")
    public void createScrapTestToFailByNullName() throws Exception{
        //given
        ScrapDto.SaveRequest saveRequest1 = SaveRequest.builder()
                .user(new User())
                .postList(new ArrayList<ScrapPost>())
                .build();


        // then
        assertThrows(NotFoundScrapNameException.class, ()-> scrapService.createScrap(saveRequest1));

    }

    @Test
    @DisplayName("서비스 createScrapCollection 예외 발생 테스트 - 포스트 리스트가 없어서 생성안된다")
    public void createScrapTestToFailByNullPostList() throws Exception{
        //given
        ScrapDto.SaveRequest saveRequest1 = SaveRequest.builder()
                .name("test")
                .user(new User())
                .build();


        // then
        assertThrows(NotFoundScrapPostListException.class, ()-> scrapService.createScrap(saveRequest1));

    }
    
    @Test
    @DisplayName("서비스 createScrap 성공 테스트 ")
    public void createScrapTest() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrapDto = createScrapDto1(user);
        
        
        // when
        Long userId = userService.saveUser(user);
        Long scrapId = scrapService.createScrap(scrapDto);
        // then
        scrapRepository.findById(scrapId).orElseThrow(NotFoundScrapByIdException::new);
    }
    // createScrap 테스트 종료





    @Test
    @DisplayName("해당 유저의 전체 스크랩 가져오는 테스트") // 코드 추가해야함, 중복에 대한 테스트도 해야함
    public void findScrapsByUserTest() throws Exception{
        //given
        User user = createUserDto1().toEntity();


        SaveRequest saveRequest1 = createScrapDto1(user);
        SaveRequest saveRequest2 = createScrapDto2(user);



        // when
        Long userId = userService.saveUser(user);
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        assertThat(2).isEqualTo(userRepository.findById(userId).get().getScraps().size());
    }




    // saveCollection 테스트 시작
    @Test
    @DisplayName("saveCollection 실패 테스트 - 스크랩 아이디 확인 불가")
    public void saveCollectionTestToFailByScrapId() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap = createScrapDto1(user);
        PostDto.SaveRequest post = createPostDto1(user);

        // when
        Long uid = userService.saveUser(user);
        Long pid = postService.SavePost(post);
        Long sid = scrapService.createScrap(scrap);

        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.saveCollection(sid + 1, pid));
    }

    @Test
    @DisplayName("saveCollection 실패 테스트 - 포스트 아이디 확인 불가")
    public void saveCollectionTestToFailByPostId() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap = createScrapDto1(user);
        PostDto.SaveRequest post = createPostDto1(user);

        // when
        Long uid = userService.saveUser(user);
        Long pid = postService.SavePost(post);
        Long sid = scrapService.createScrap(scrap);

        // then
        assertThrows(NotFoundPostByIdException.class, () -> scrapService.saveCollection(sid, pid+1));
    }

    @Test
    @DisplayName("saveCollection 실패 테스트 - 중복된 포스트 저장 요청")
    public void saveCollectionTestToFailByDuplicateScrapPost() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap = createScrapDto1(user);
        PostDto.SaveRequest post = createPostDto1(user);

        // when
        Long uid = userService.saveUser(user);
        Long pid = postService.SavePost(post);
        Long sid = scrapService.createScrap(scrap);
        scrapService.saveCollection(sid, pid);

        // then
        assertThrows(DuplicateScrapPostException.class, () -> scrapService.saveCollection(sid, pid));
    }

    @Test
    @DisplayName("saveCollection 성공 테스트 - 스크랩 안에 포스트 들어간 포스트 수 확인")
    public void saveCollectionTestByPostsNum() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap = createScrapDto1(user);
        PostDto.SaveRequest post1 = createPostDto1(user);
        PostDto.SaveRequest post2 = createPostDto2(user);

        // when
        Long uid = userService.saveUser(user);
        Long pid1 = postService.SavePost(post1);
        Long pid2 = postService.SavePost(post2);
        Long sid = scrapService.createScrap(scrap);
        scrapService.saveCollection(sid, pid1);
        scrapService.saveCollection(sid, pid2);
        // then
        assertThat(2).isEqualTo(scrapRepository.findById(sid).get().getPostList().size());
    }

    @Test
    @DisplayName("saveCollection 성공 테스트 - 스크랩 포스트에 스크랩과 포스트를 저장한 스크랩 일치 확인")
    public void saveCollectionTestByScrapPost() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap = createScrapDto1(user);
        PostDto.SaveRequest post = createPostDto1(user);


        // when
        Long uid = userService.saveUser(user);
        Long pid = postService.SavePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid, pid);
        // then
        assertThat(scrapRepository.findById(sid).get()).isEqualTo(scrapPostRepository.findById(spid).get().getScrap());
    }
    // saveCollection 테스트 종료

    // updateCollectionName 테스트 시작
    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 실패 테스트 - 스크랩 존재 x")
    public void updateNameTestToFailByScrapId() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(user);
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(user);
        // when
        Long userId = userService.saveUser(user);
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.updateCollectionName(scrap2 + 1, userId, "test3"));

    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 실패 테스트 - 유저 존재 x")
    public void updateNameTestToFailByUserId() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(user);
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(user);
        // when
        Long userId = userService.saveUser(user);
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        assertThrows(NotFoundUserException.class, () -> scrapService.updateCollectionName(scrap2 , userId+1, "test3"));

    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 실패 테스트 - 이름 중복")
    public void updateNameTestToFailByDuplicateName() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(user);
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(user);
        // when
        Long userId = userService.saveUser(user);
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        assertThrows(DuplicateScrapNameException.class, () -> scrapService.updateCollectionName(scrap1, userId, "test2"));

    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 성공 테스트 - 같은 유저에서 다른 이름으로 변경")
    public void updateNameTestSameUser() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(user);
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(user);
        // when
        Long userId = userService.saveUser(user);
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        scrapService.updateCollectionName(scrap1, userId, "test3");
        assertThat("test3").isEqualTo(scrapRepository.findById(scrap1).get().getName());
    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 성공 테스트 - 다른 유저에서 같은 이름으로 변경")
    public void updateNameTestDifferentUser() throws Exception{
        //given
        User user1 = createUserDto1().toEntity();
        User user2 = createUserDto2().toEntity();
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(user1);
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(user2);

        // when
        Long user1Id = userService.saveUser(user1);
        Long user2Id = userService.saveUser(user2);
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        scrapService.updateCollectionName(scrap1, user1Id, "test2");
        assertThat("test2").isEqualTo(scrapRepository.findById(scrap1).get().getName());
    }
    // updateCollectionName 테스트 종료


    // deleteCollection 테스트 시작
    @Test
    @DisplayName("스크랩 리포지토리에서 스크랩 삭제 실패 테스트 - 아이디 없어서 삭제 실패")
    public void deleteCollectionTestToFail() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        SaveRequest saveRequest = createScrapDto1(user);

        // when
        Long userId = userService.saveUser(user);
        Long scrap = scrapService.createScrap(saveRequest);
        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.deleteCollection(scrap+1));

    }

    @Test
    @DisplayName("스크랩 리포지토리에서 스크랩 삭제 성공 테스트")
    public void deleteCollectionTest() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        SaveRequest saveRequest = createScrapDto1(user);

        // when
        Long userId = userService.saveUser(user);
        Long scrap = scrapService.createScrap(saveRequest);

        // then
        String ok = scrapService.deleteCollection(scrap);
        assertThat(ok).isEqualTo("ok");
    }
    // deleteCollection 테스트 종료


    // deleteCollectionItem 테스트 시작
    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 스크랩 아이디 존재 x")
    public void deleteCollectionItemTesetToFailByScrapId() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap = createScrapDto1(user);
        PostDto.SaveRequest post = createPostDto1(user);


        // when
        Long uid = userService.saveUser(user);
        Long pid = postService.SavePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid, pid);
        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.deleteCollectionItem(sid + 1, pid));
    }

    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 포스트 아이디 존재 x")
    public void deleteCollectionItemTestToFailByPostId() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap = createScrapDto1(user);
        PostDto.SaveRequest post = createPostDto1(user);


        // when
        Long uid = userService.saveUser(user);
        Long pid = postService.SavePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid, pid);
        // then
        assertThrows(NotFoundPostByIdException.class, () -> scrapService.deleteCollectionItem(sid , pid+1));
    }

    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 스크랩 포스트 객체 존재 x")
    public void deleteCollectionItemTestToFailByScrapPost() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap1 = createScrapDto1(user);
        ScrapDto.SaveRequest scrap2 = createScrapDto2(user);
        PostDto.SaveRequest post = createPostDto1(user);


        // when
        Long uid = userService.saveUser(user);
        Long pid = postService.SavePost(post);
        Long sid1 = scrapService.createScrap(scrap1);
        Long sid2 = scrapService.createScrap(scrap2);
        Long spid = scrapService.saveCollection(sid1, pid);
        // then
        assertThrows(NotFoundScrapPostByScrapAndPostException.class, () -> scrapService.deleteCollectionItem(sid2 , pid));
    }

    @Test
    @DisplayName("deleteCollectionItem 성공 테스트 - 스크랩 포스트 리스트에 존재 x")
    public void deleteCollectionItemTestByPostList() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap = createScrapDto1(user);
        PostDto.SaveRequest post = createPostDto1(user);


        // when
        Long uid = userService.saveUser(user);
        Long pid = postService.SavePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid, pid);
        // then
        scrapService.deleteCollectionItem(sid, pid);
        assertThat(0).isEqualTo(scrapRepository.findById(sid).get().getPostList().size());


    }
    @Test
    @DisplayName("deleteCollectionItem 성공 테스트 - 포스트 스크랩 리스트에 존재 x")
    public void deleteCollectionItemTestByScrapList() throws Exception{
        //given
        User user = createUserDto1().toEntity();
        ScrapDto.SaveRequest scrap = createScrapDto1(user);
        PostDto.SaveRequest post = createPostDto1(user);


        // when
        Long uid = userService.saveUser(user);
        Long pid = postService.SavePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid, pid);
        // then
        scrapService.deleteCollectionItem(sid, pid);
        assertThat(0).isEqualTo(postRepository.findById(pid).get().getScrapList().size());


    }
    // deleteCollectionItem 테스트 종료
}