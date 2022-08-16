package com.f3f.community.service;


import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.categoryException.MaxDepthException;
import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.scrapException.*;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.ScrapPost;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.repository.ScrapPostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.post.service.ScrapPostService;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScrapServiceTestWithDB {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScrapService scrapService;
    @Autowired
    ScrapRepository scrapRepository;
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    ScrapPostService scrapPostService;
    @Autowired
    ScrapPostRepository scrapPostRepository;


    private List<Long> createUsers(int n) {
        List<Long> uids = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            UserDto.SaveRequest temp = createUserDto("guest" + i);
            Long uid = userService.saveUser(temp);
            uids.add(uid);
        }
        return uids;
    }

    private List<Long> createCategories(int n) throws Exception {
        List<Long> cids = new ArrayList<>();
        cids.add(createRoot());
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            while (true) {
                try {
                    CategoryDto.SaveRequest categoryDto = createCategoryDto("cat"+i, categoryRepository.findById(cids.get(random.nextInt(i+1))).get());
                    Long cid = categoryService.createCategory(categoryDto);
                    cids.add(cid);
                    break;
                } catch (MaxDepthException e) {
                    continue;
                }
            }
        }

        return cids;
    }

    private List<Long> createPosts(List<Long> users, List<Long> categories, int n) throws Exception{
        List<Long> pids = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            PostDto.SaveRequest postDto = createPostDto("title"+i, userRepository.findById(users.get(random.nextInt(users.size()))).get(), categoryRepository.findById(categories.get(random.nextInt(categories.size()))).get());

            Long pid = postService.savePost(postDto);
            pids.add(pid);
        }

        return pids;
    }

    private List<Long> createScraps(List<Long> users, List<Long> posts, int n) throws Exception{
        List<Long> sids = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            Long uid = users.get(random.nextInt(users.size()));
            ScrapDto.SaveRequest scrapDto = createScrapDto(userRepository.findById(uid).get(), "scrap" + i);
            Long sid = scrapService.createScrap(scrapDto);
            sids.add(sid);
            int count = (int) posts.size() / 5;
            for (int j = 0; j < count; j++) {
                try {
                    scrapService.saveCollection(sid, uid, posts.get(random.nextInt(posts.size())));
                } catch (DuplicateScrapPostException e) {
                    System.out.println(e.getMessage());
                }
            }

        }

        return sids;
    }

    private UserDto.SaveRequest createUserDto(String name) {
        return UserDto.SaveRequest.builder()
                .email(name+"@"+name+".com")
                .userGrade(UserGrade.GOLD)
                .phone("010123457678")
                .nickname("nick"+name)
                .password("a12345678@")
                .build();
    }

    private Long createRoot() throws Exception {
        CategoryDto.SaveRequest cat = createCategoryDto("root", null);
        Long rid = categoryService.createCategory(cat);
        return categoryRepository.findById(rid).get().getId();
    }

    private CategoryDto.SaveRequest createCategoryDto(String name, Category parent) {
        return CategoryDto.SaveRequest.builder()
                .categoryName(name)
                .childCategory(new ArrayList<>())
                .parents(parent)
                .postList(new ArrayList<>()).build();
    }

    private ScrapDto.SaveRequest createScrapDto(User user, String name) {
        return ScrapDto.SaveRequest.builder()
                .name(name)
                .postList(new ArrayList<>())
                .user(user)
                .build();
    }

    private PostDto.SaveRequest createPostDto(String title, User user, Category cat) {
        return PostDto.SaveRequest.builder()
                .title(title)
                .content("temp content")
                .author(user)
                .scrapList(new ArrayList<>())
                .category(cat).build();
    }

    @Before
    public void deleteAll() {
        scrapPostRepository.deleteAll();
        scrapRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("디비에 데이터 들어가는 테스트")
    public void dbInsertionTest() throws Exception{
        //given
        UserDto.SaveRequest temp = createUserDto("temp");
        Long uid = userService.saveUser(temp);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest postDto = createPostDto("tempPost", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);
        ScrapDto.SaveRequest scrapDto = createScrapDto(userRepository.findById(uid).get(), "tempScrap");
        Long sid = scrapService.createScrap(scrapDto);

        // when
        scrapService.saveCollection(sid,uid, pid);
        List<Post> result = scrapPostService.getPostsOfScrap(sid);
        // then
        assertThat(result.get(0).getId()).isEqualTo(postRepository.findById(pid).get().getId());


    }

    @Test
    @DisplayName("객체들 자동 생성 테스트")
    public void createAutomationTest() throws Exception{
        //given
        List<Long> users = createUsers(10);
        List<Long> categories = createCategories(20);
        List<Long> posts = createPosts(users, categories, 20);
        List<Long> scraps = createScraps(users, posts, 10);

        // when

        // then
        
    }

    @Test
    @DisplayName("createScrap 실패 테스트 - 중복 되는 스크랩 이름")
    public void createScrapTestToFailByDuplicateName() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid1 = scrapService.createScrap(scrapDto);

        // when
        ScrapDto.SaveRequest duplicate = createScrapDto(userRepository.findById(uid).get(), "temp");

        // then
        assertThrows(DuplicateScrapNameException.class, () -> scrapService.createScrap(duplicate));
    }


    @Test
    @DisplayName("saveCollection 실패 테스트 - 중복 포스트")
    public void saveCollectionTestToFailByDuplicatePost() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest postDto = createPostDto("title", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);

        // when
        scrapService.saveCollection(sid, uid, pid);

        // then
        assertThrows(DuplicateScrapPostException.class, () -> scrapService.saveCollection(sid, uid, pid));
    }

    @Test
    @DisplayName("saveCollection 테스트 아이디로 검증")
    public void saveCollectionTest() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest postDto = createPostDto("title", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);
        // when
        Long spid = scrapService.saveCollection(sid, uid, pid);
        List<ScrapPost> posts = scrapPostRepository.findScrapPostsByScrap(scrapRepository.findById(sid).get());
        // then
        assertThat(posts.get(0).getId()).isEqualTo(spid);
    }

    @Test
    @DisplayName("updateCollectionName 실패 테스트 - 이름 중복")
    public void updateCollectionNameTestToFailByDuplicateName() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid1 = scrapService.createScrap(scrapDto1);
        ScrapDto.SaveRequest scrapDto2 = createScrapDto(userRepository.findById(uid).get(), "temp2");
        Long sid2 = scrapService.createScrap(scrapDto2);

        // then
        assertThrows(DuplicateScrapNameException.class, () -> scrapService.updateCollectionName(sid2, uid, "temp"));
    }

    @Test
    @DisplayName("updateCollectionName 실패 테스트 - null 이름")
    public void updateCollectionNameTestToFailByNullNewName() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid1 = scrapService.createScrap(scrapDto1);
        ScrapDto.SaveRequest scrapDto2 = createScrapDto(userRepository.findById(uid).get(), "temp2");
        Long sid2 = scrapService.createScrap(scrapDto2);

        // then
        assertThrows(NotFoundNewScrapNameException.class, () -> scrapService.updateCollectionName(sid2, uid, null));
    }

    @Test
    @DisplayName("updateCollectionName 실패 테스트 - empty 이름")
    public void updateCollectionNameTestToFailByEmptyName() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid1 = scrapService.createScrap(scrapDto1);
        ScrapDto.SaveRequest scrapDto2 = createScrapDto(userRepository.findById(uid).get(), "temp2");
        Long sid2 = scrapService.createScrap(scrapDto2);

        // then
        assertThrows(NotFoundNewScrapNameException.class, () -> scrapService.updateCollectionName(sid1, uid, ""));
    }

    @Test
    @DisplayName("updateCollectionName 성공 테스트")
    public void updateCollectionNameTest() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid1 = scrapService.createScrap(scrapDto1);
        ScrapDto.SaveRequest scrapDto2 = createScrapDto(userRepository.findById(uid).get(), "temp2");
        Long sid2 = scrapService.createScrap(scrapDto2);


        // when
        scrapService.updateCollectionName(sid1, uid, "temp3");
        // then
        assertThat("temp3").isEqualTo(scrapRepository.findById(sid1).get().getName());
    }

    @Test
    @DisplayName("deleteCollection 실패 테스트 - 존재하지 않는 스크랩 아이디")
    public void deleteCollectionTestToFailByScrapId() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid1 = scrapService.createScrap(scrapDto1);

        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.deleteCollection(sid1 + 1, uid));
    }

    @Test
    @DisplayName("deleteCollection 실패 테스트 - 다른 유저의 스크랩 컬렉션 삭제")
    public void deleteCollectionTestToFailByWrongUser() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        UserDto.SaveRequest userDto2 = createUserDto("temp2");
        Long uid2 = userService.saveUser(userDto2);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto1);

        // then
        assertThrows(NotFoundScrapByUserException.class, () -> scrapService.deleteCollection(sid, uid2));
    }

    @Test
    @DisplayName("deleteCollection 성공 테스트 - 포스트 없는 빈 스크랩")
    public void deleteCollectionTestNoPosts() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto1);
        // when
        scrapService.deleteCollection(sid, uid);
        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapRepository.findById(sid).orElseThrow(NotFoundScrapByIdException::new));
    }

    @Test
    @DisplayName("deleteCollection 성공 테스트 - 포스트 있는 스크랩")
    public void deleteCollectionTestWithPosts() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto1);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("cat1", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest post1 = createPostDto("post1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest post2 = createPostDto("post2", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid1 = postService.savePost(post1);
        Long pid2 = postService.savePost(post2);

        // when
        Long spid1 = scrapService.saveCollection(sid, uid, pid1);
        Long spid2 = scrapService.saveCollection(sid, uid, pid2);

        scrapService.deleteCollection(sid, uid);
        // then
        assertThrows(NotFoundScrapPostByIdException.class, () -> scrapPostRepository.findById(spid1).orElseThrow(NotFoundScrapPostByIdException::new));

    }

    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 스크랩 아이디 못 찾음")
    public void deleteCollectionItemTestToFailByScrapId() throws Exception{
        // given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto1);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("cat1", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest post1 = createPostDto("post1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest post2 = createPostDto("post2", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid1 = postService.savePost(post1);
        Long pid2 = postService.savePost(post2);

        // when
        Long spid1 = scrapService.saveCollection(sid, uid, pid1);
        Long spid2 = scrapService.saveCollection(sid, uid, pid2);
        // then
        assertThrows(NotFoundScrapByIdException.class, () -> scrapService.deleteCollectionItem(sid + 1, uid, pid1));
    }

    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 스크랩한 유저가 아닌 다른 유저로 접근")
    public void deleteCollectionItemTesToFailByWrongUser() throws Exception{
        // given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto1);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("cat1", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest post1 = createPostDto("post1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest post2 = createPostDto("post2", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid1 = postService.savePost(post1);
        Long pid2 = postService.savePost(post2);

        // when
        Long spid1 = scrapService.saveCollection(sid, uid, pid1);
        Long spid2 = scrapService.saveCollection(sid, uid, pid2);
        // then
        assertThrows(NotFoundScrapByUserException.class, () -> scrapService.deleteCollectionItem(sid, uid + 1, pid1));
    }

    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 저장되지 않은 포스트")
    public void deleteCollectionItemTestToFailByWrongPost() throws Exception{
        // given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto1);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("cat1", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest post1 = createPostDto("post1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest post2 = createPostDto("post2", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid1 = postService.savePost(post1);
        Long pid2 = postService.savePost(post2);

        // when
        Long spid1 = scrapService.saveCollection(sid, uid, pid1);
        Long spid2 = scrapService.saveCollection(sid, uid, pid2);
        // then
        assertThrows(NotFoundPostByIdException.class, () -> scrapService.deleteCollectionItem(sid, uid, pid2 + 1));
    }


    @Test
    @DisplayName("deleteCollectionItem 실패 테스트 - 해당 스크랩에 존재하지 않는 포스트에 대한 삭제 요청")
    public void deleteCollectionItemTestToFailByScrapPost() throws Exception{
        // given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto1);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("cat1", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest post1 = createPostDto("post1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest post2 = createPostDto("post2", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid1 = postService.savePost(post1);
        Long pid2 = postService.savePost(post2);

        // when
        Long spid1 = scrapService.saveCollection(sid, uid, pid1);
        // then
        assertThrows(NotFoundScrapPostByScrapAndPostException.class, () -> scrapService.deleteCollectionItem(sid, uid, pid2));
    }

    @Test
    @DisplayName("deleteCollectionItem 성공 테스트 - 아이디로 검증")
    public void deleteCollectionItemTest() throws Exception{
        // given
        UserDto.SaveRequest userDto = createUserDto("temp");
        Long uid = userService.saveUser(userDto);
        ScrapDto.SaveRequest scrapDto1 = createScrapDto(userRepository.findById(uid).get(), "temp");
        Long sid = scrapService.createScrap(scrapDto1);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("cat1", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest post1 = createPostDto("post1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest post2 = createPostDto("post2", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid1 = postService.savePost(post1);
        Long pid2 = postService.savePost(post2);

        // when
        Long spid1 = scrapService.saveCollection(sid, uid, pid1);
        Long spid2 = scrapService.saveCollection(sid, uid, pid2);
        scrapService.deleteCollectionItem(sid, uid, pid2);
        List<ScrapPost> posts = scrapPostRepository.findScrapPostsByScrap(scrapRepository.findById(sid).get());
        // then
        assertThrows(NotFoundScrapPostByIdException.class, () -> scrapPostRepository.findById(spid2).orElseThrow(NotFoundScrapPostByIdException::new));


    }
}
