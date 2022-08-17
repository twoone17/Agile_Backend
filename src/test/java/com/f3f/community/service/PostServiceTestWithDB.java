package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.categoryException.MaxDepthException;
import com.f3f.community.exception.scrapException.DuplicateScrapPostException;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostServiceTestWithDB {
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
    ScrapPostRepository scrapPostRepository;

    @Autowired
    ScrapPostService scrapPostService;


    private List<Long> createUsers(int n){
        List<Long> uids = new ArrayList<>();
        for(int i = 0; i<n; i++){
            UserDto.SaveRequest temp = createUserDto("guest" + i);
            Long uid = userService.saveUser(temp);
            uids.add(uid);
        }

        return uids;
    }
    private Long createRoot() throws Exception {
        CategoryDto.SaveRequest cat = createCategoryDto("root", null);
        Long rid = categoryService.createCategory(cat);
        return categoryRepository.findById(rid).get().getId();
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
        for(int i =0 ; i < n ; i++){
            PostDto.SaveRequest postDto = createPostDto("title" + i, userRepository.findById(users.get(random.nextInt(users.size()))).get(), categoryRepository.findById(categories.get(random.nextInt(categories.size()))).get());
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
                    scrapService.saveCollection(sid,uid,posts.get(random.nextInt(posts.size())));
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
    @DisplayName("객체들 자동 생성 테스트")
    public void createAutomationTest() throws Exception{
        //given
        List<Long> users = createUsers(20);
        List<Long> categories = createCategories(3);
        List<Long> posts = createPosts(users, categories, 7);
        List<Long> scraps = createScraps(users, posts, 5);

        // when

        // then
    }

    @Test
    @DisplayName("디비에 데이터 들어가는 테스트")
    public void dbInsertionTest() throws Exception{
        //given
        UserDto.SaveRequest userSave = createUserDto("euisung");
        Long uid = userService.saveUser(userSave);
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


    /*************************************************************************************
     * 게시글 작성 테스트 (Create)
     **************************************************************************************/
    @Test
    @DisplayName("1 Save-1: savePost 성공 테스트")
    public void savePostTestWithDBToOk() throws Exception{
    //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        PostDto.SaveRequest postDto = createPostDto("tempPost", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);

        ScrapDto.SaveRequest scrapDto = createScrapDto(userRepository.findById(uid).get(), "tempScrap");
        Long sid = scrapService.createScrap(scrapDto);
        //when

    //then

    }

}
