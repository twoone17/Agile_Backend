package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.scrapException.*;
import com.f3f.community.exception.userException.NotFoundUserException;
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
class
ScrapServiceTest {

    @Autowired
    ScrapService scrapService;

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScrapPostRepository scrapPostRepository;

    @Autowired
    CategoryRepository categoryRepository;

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

    private PostDto.SaveRequest createPostDto1(User user, Category cat) {
        return PostDto.SaveRequest.builder()
                .title("test title")
                .content("test content for test")
                .author(user)
                .scrapList(new ArrayList<>())
                .category(cat)
                .build();
    }

    private Category createRoot() throws Exception{
        CategoryDto.SaveRequest cat = createCategoryDto("root", null);
        Long rid = categoryService.createCategory(cat);
        return categoryRepository.findById(rid).get();
    }

    private PostDto.SaveRequest createPostDto2(User user, Category cat) {
        return PostDto.SaveRequest.builder()
                .title("test title2")
                .content("test content for test2")
                .author(user)
                .scrapList(new ArrayList<>())
                .category(cat)
                .build();
    }

    private CategoryDto.SaveRequest createCategoryDto(String name, Category parent) {
        return CategoryDto.SaveRequest.builder()
                .categoryName(name)
                .childCategory(new ArrayList<>())
                .parents(parent)
                .postList(new ArrayList<>()).build();
    }



    // createScrap 테스트 시작

    
    @Test
    @DisplayName("서비스 createScrap 성공 테스트 ")
    public void createScrapTest() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long userId = userService.saveUser(user);
        ScrapDto.SaveRequest scrapDto = createScrapDto1(userRepository.findById(userId).get());


        // when
        Long scrapId = scrapService.createScrap(scrapDto);
        // then
        scrapRepository.findById(scrapId).orElseThrow(NotFoundScrapByIdException::new);
    }
    // createScrap 테스트 종료





    @Test
    @DisplayName("해당 유저의 전체 스크랩 가져오는 테스트") // 코드 추가해야함, 중복에 대한 테스트도 해야함
    public void findScrapsByUserTest() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();

        Long userId = userService.saveUser(user);

        SaveRequest saveRequest1 = createScrapDto1(userRepository.findById(userId).get());
        SaveRequest saveRequest2 = createScrapDto2(userRepository.findById(userId).get());



        // when
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
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap = createScrapDto1(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post = createPostDto1(userRepository.findById(uid).get(), cat);

        // when
        Long pid = postService.savePost(post);
        Long sid = scrapService.createScrap(scrap);

        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.saveCollection(sid + 1,uid, pid));
    }

    @Test
    @DisplayName("saveCollection 실패 테스트 - 포스트 아이디 확인 불가")
    public void saveCollectionTestToFailByPostId() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap = createScrapDto1(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post = createPostDto1(userRepository.findById(uid).get(), cat);

        // when
        Long pid = postService.savePost(post);
        Long sid = scrapService.createScrap(scrap);

        // then
        assertThrows(NotFoundPostByIdException.class, () -> scrapService.saveCollection(sid, uid,pid+1));
    }

    @Test
    @DisplayName("saveCollection 실패 테스트 - 중복된 포스트 저장 요청")
    public void saveCollectionTestToFailByDuplicateScrapPost() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap = createScrapDto1(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post = createPostDto1(userRepository.findById(uid).get(), cat);

        // when
        Long pid = postService.savePost(post);
        Long sid = scrapService.createScrap(scrap);
        scrapService.saveCollection(sid,uid, pid);

        // then
        assertThrows(DuplicateScrapPostException.class, () -> scrapService.saveCollection(sid,uid, pid));
    }

    @Test
    @DisplayName("saveCollection 성공 테스트 - 스크랩 안에 포스트 들어간 포스트 수 확인")
    public void saveCollectionTestByPostsNum() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap = createScrapDto1(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post1 = createPostDto1(userRepository.findById(uid).get(), cat);
        PostDto.SaveRequest post2 = createPostDto2(userRepository.findById(uid).get(), cat);

        // when
        Long pid1 = postService.savePost(post1);
        Long pid2 = postService.savePost(post2);
        Long sid = scrapService.createScrap(scrap);
        scrapService.saveCollection(sid,uid, pid1);
        scrapService.saveCollection(sid,uid, pid2);
        // then
        assertThat(2).isEqualTo(scrapRepository.findById(sid).get().getPostList().size());
    }

    @Test
    @DisplayName("saveCollection 성공 테스트 - 스크랩 포스트에 스크랩과 포스트를 저장한 스크랩 일치 확인")
    public void saveCollectionTestByScrapPost() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap = createScrapDto1(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post = createPostDto1(userRepository.findById(uid).get(), cat);


        // when
        Long pid = postService.savePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid,uid, pid);
        // then
        assertThat(scrapRepository.findById(sid).get()).isEqualTo(scrapPostRepository.findById(spid).get().getScrap());
    }
    // saveCollection 테스트 종료

    // updateCollectionName 테스트 시작
    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 실패 테스트 - 스크랩 존재 x")
    public void updateNameTestToFailByScrapId() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(userRepository.findById(uid).get());
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(userRepository.findById(uid).get());
        // when
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.updateCollectionName(scrap2 + 1, uid, "test3"));

    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 실패 테스트 - 유저 존재 x")
    public void updateNameTestToFailByUserId() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(userRepository.findById(uid).get());
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(userRepository.findById(uid).get());
        // when
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        assertThrows(NotFoundUserException.class, () -> scrapService.updateCollectionName(scrap2 , uid+1, "test3"));

    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 실패 테스트 - 이름 중복")
    public void updateNameTestToFailByDuplicateName() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(userRepository.findById(uid).get());
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(userRepository.findById(uid).get());
        // when
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        assertThrows(DuplicateScrapNameException.class, () -> scrapService.updateCollectionName(scrap1, uid, "test2"));

    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 실패 테스트 - null 이름")
    public void updateNameTestToFailByNullName() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(userRepository.findById(uid).get());
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(userRepository.findById(uid).get());
        // when
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        assertThrows(NotFoundNewScrapNameException.class, () -> scrapService.updateCollectionName(scrap1, uid, null));

    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 실패 테스트 - 빈 이름")
    public void updateNameTestToFailByEmptyName() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(userRepository.findById(uid).get());
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(userRepository.findById(uid).get());
        // when
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        assertThrows(NotFoundNewScrapNameException.class, () -> scrapService.updateCollectionName(scrap1, uid, ""));

    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 성공 테스트 - 같은 유저에서 다른 이름으로 변경")
    public void updateNameTestSameUser() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(userRepository.findById(uid).get());
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(userRepository.findById(uid).get());
        // when
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        scrapService.updateCollectionName(scrap1, uid, "test3");
        assertThat("test3").isEqualTo(scrapRepository.findById(scrap1).get().getName());
    }

    @Test
    @DisplayName("스크랩 컬렉션 이름 변경 성공 테스트 - 다른 유저에서 같은 이름으로 변경")
    public void updateNameTestDifferentUser() throws Exception{
        //given
        UserDto.SaveRequest user1 = createUserDto1();
        UserDto.SaveRequest user2 = createUserDto2();
        Long uid1 = userService.saveUser(user1);
        Long uid2 = userService.saveUser(user2);
        ScrapDto.SaveRequest saveRequest1 = createScrapDto1(userRepository.findById(uid1).get());
        ScrapDto.SaveRequest saveRequest2 = createScrapDto2(userRepository.findById(uid2).get());

        // when
        Long scrap1 = scrapService.createScrap(saveRequest1);
        Long scrap2 = scrapService.createScrap(saveRequest2);
        // then
        scrapService.updateCollectionName(scrap1, uid1, "test2");
        assertThat("test2").isEqualTo(scrapRepository.findById(scrap1).get().getName());
    }
    // updateCollectionName 테스트 종료


    // deleteCollection 테스트 시작
    @Test
    @DisplayName("스크랩 리포지토리에서 스크랩 삭제 실패 테스트 - 아이디 없어서 삭제 실패")
    public void deleteCollectionTestToFail() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        SaveRequest saveRequest = createScrapDto1(userRepository.findById(uid).get());

        // when
        Long scrap = scrapService.createScrap(saveRequest);
        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.deleteCollection(scrap+1, uid));

    }

    @Test
    @DisplayName("스크랩 리포지토리에서 스크랩 삭제 성공 테스트")
    public void deleteCollectionTest() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        SaveRequest saveRequest = createScrapDto1(userRepository.findById(uid).get());

        // when
        Long scrap = scrapService.createScrap(saveRequest);

        // then
        String ok = scrapService.deleteCollection(scrap,uid);
        assertThrows(NotFoundScrapByIdException.class, () -> scrapRepository.findById(scrap).orElseThrow(NotFoundScrapByIdException::new));
    }
    // deleteCollection 테스트 종료


    // deleteCollectionItem 테스트 시작
    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 스크랩 아이디 존재 x")
    public void deleteCollectionItemTestToFailByScrapId() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap = createScrapDto1(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post = createPostDto1(userRepository.findById(uid).get(), cat);


        // when
        Long pid = postService.savePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid, uid, pid);
        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.deleteCollectionItem(sid + 1,uid, pid));
    }

    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 포스트 아이디 존재 x")
    public void deleteCollectionItemTestToFailByPostId() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap = createScrapDto1(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post = createPostDto1(userRepository.findById(uid).get(), cat);


        // when
        Long pid = postService.savePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid,uid, pid);
        // then
        assertThrows(NotFoundPostByIdException.class, () -> scrapService.deleteCollectionItem(sid , uid, pid+1));
    }

    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 스크랩 포스트 객체 존재 x")
    public void deleteCollectionItemTestToFailByScrapPost() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap1 = createScrapDto1(userRepository.findById(uid).get());
        ScrapDto.SaveRequest scrap2 = createScrapDto2(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post = createPostDto1(userRepository.findById(uid).get(), cat);


        // when
        Long pid = postService.savePost(post);
        Long sid1 = scrapService.createScrap(scrap1);
        Long sid2 = scrapService.createScrap(scrap2);
        Long spid = scrapService.saveCollection(sid1,uid, pid);
        // then
        assertThrows(NotFoundScrapPostByScrapAndPostException.class, () -> scrapService.deleteCollectionItem(sid2 , uid, pid));
    }

    @Test
    @DisplayName("deleteCollectionItem 성공 테스트 - 스크랩 포스트 리스트에 존재 x")
    public void deleteCollectionItemTestByPostList() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap = createScrapDto1(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post = createPostDto1(userRepository.findById(uid).get(), cat);


        // when
        Long pid = postService.savePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid,uid,pid);
        // then
        scrapService.deleteCollectionItem(sid, uid, pid);
        assertThat(0).isEqualTo(scrapRepository.findById(sid).get().getPostList().size());


    }
    @Test
    @DisplayName("deleteCollectionItem 성공 테스트 - 포스트 스크랩 리스트에 존재 x")
    public void deleteCollectionItemTestByScrapList() throws Exception{
        //given
        UserDto.SaveRequest user = createUserDto1();
        Long uid = userService.saveUser(user);
        ScrapDto.SaveRequest scrap = createScrapDto1(userRepository.findById(uid).get());
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest post = createPostDto1(userRepository.findById(uid).get(), cat);


        // when
        Long pid = postService.savePost(post);
        Long sid = scrapService.createScrap(scrap);
        Long spid = scrapService.saveCollection(sid,uid, pid);
        // then
        scrapService.deleteCollectionItem(sid, uid, pid);
        assertThat(0).isEqualTo(postRepository.findById(pid).get().getScrapList().size());


    }
    // deleteCollectionItem 테스트 종료
}