package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.categoryException.MaxDepthException;
import com.f3f.community.exception.common.DuplicateException;
import com.f3f.community.exception.common.NotFoundByIdException;
import com.f3f.community.exception.scrapException.DuplicateScrapPostException;
import com.f3f.community.exception.tagException.DuplicateTagNameException;
import com.f3f.community.exception.tagException.NotFoundPostTagException;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.repository.ScrapPostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.post.service.ScrapPostService;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.tag.dto.TagDto;
import com.f3f.community.tag.repository.PostTagRepository;
import com.f3f.community.tag.repository.TagRepository;
import com.f3f.community.tag.service.TagService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TagServiceTest {
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

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostTagRepository postTagRepository;


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
                    CategoryDto.SaveRequest categoryDto = createCategoryDto("cat" + i, categoryRepository.findById(cids.get(random.nextInt(i + 1))).get());
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

    private List<Long> createPosts(List<Long> users, List<Long> categories, int n) throws Exception {
        List<Long> pids = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            PostDto.SaveRequest postDto = createPostDto("title" + i, userRepository.findById(users.get(random.nextInt(users.size()))).get(), categoryRepository.findById(categories.get(random.nextInt(categories.size()))).get());

            Long pid = postService.savePost(postDto);
            pids.add(pid);
        }

        return pids;
    }

    private List<Long> createScraps(List<Long> users, List<Long> posts, int n) throws Exception {
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

    private List<Long> createTags(int n) throws Exception {
        List<Long> tids = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            TagDto.SaveRequest tagDto = createTagDto("tag" + i);
            Long tid = tagService.createTag(tagDto);
            tids.add(tid);
        }
        return tids;
    }

    private UserDto.SaveRequest createUserDto(String name) {
        return UserDto.SaveRequest.builder()
                .email(name + "@" + name + ".com")
                .userGrade(UserGrade.GOLD)
                .phone("010123457678")
                .nickname("nick" + name)
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

    private TagDto.SaveRequest createTagDto(String name) {
        return TagDto.SaveRequest.builder()
                .tagName(name).build();
    }

    @Before
    public void deleteAll() {
        postTagRepository.deleteAll();
        tagRepository.deleteAll();
        scrapPostRepository.deleteAll();
        scrapRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("객체들 자동 생성 테스트")
    public void createAutomationTest() throws Exception {
        //given
        List<Long> users = createUsers(10);
        List<Long> categories = createCategories(20);
        List<Long> posts = createPosts(users, categories, 20);
        List<Long> scraps = createScraps(users, posts, 10);
        List<Long> tags = createTags(10);
        // when

        // then

    }

    @Test
    @DisplayName("중복 태그 이름으로 생성실패")
    public void createTagTestToFailByDuplicateName() throws Exception{
        //given
        TagDto.SaveRequest test = createTagDto("test");
        Long tid = tagService.createTag(test);
        // when
        TagDto.SaveRequest test2 = createTagDto("test");

        // then
        assertThrows(DuplicateTagNameException.class, () -> tagService.createTag(test2));
    }

    @Test
    @DisplayName("포스트에 태그 추가하는 테스트")
    public void addTagToPostTest() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test = createTagDto("test");
        Long tid = tagService.createTag(test);

        // when
        Long tpid = tagService.addTagToPost(tid, posts.get(0));
        // then
        assertThat(tpid).isEqualTo(postTagRepository.findByPostAndTag(postRepository.findById(posts.get(0)).get(), tagRepository.findById(tid).get()).get().getId());
    }

    @Test
    @DisplayName("포스트에 태그 추가 실패 테스트 - 중복된 태그")
    public void addTagToPostTestToFailByDuplicateTag() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test1 = createTagDto("test");
        Long tid1 = tagService.createTag(test1);

        // when
        Long tpid = tagService.addTagToPost(tid1, posts.get(0));
        // then
        assertThrows(DuplicateException.class, () -> tagService.addTagToPost(tid1, posts.get(0)));
    }

    @Test
    @DisplayName("포스트에 태그 추가 실패 테스트 - 잘못된 태그 아이디")
    public void addTagToPostTestToFailByTagId() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test1 = createTagDto("test");
        Long tid1 = tagService.createTag(test1);


        // then
        assertThrows(NotFoundByIdException.class, () -> tagService.addTagToPost(tid1 + 1, posts.get(0)));
    }

    @Test
    @DisplayName("포스트에 태그 추가 실패 테스트 - 잘못된 포스트 아이디")
    public void addTagToPostTestToFailByPostId() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test1 = createTagDto("test");
        Long tid1 = tagService.createTag(test1);

        // then
        assertThrows(NotFoundByIdException.class, () -> tagService.addTagToPost(tid1, posts.get(4)+1));
    }

    @Test
    @DisplayName("포스트에 있는 태그 삭제하는 테스트")
    public void deletePostTagTest() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test = createTagDto("test");
        Long tid = tagService.createTag(test);

        // when
        Long tpid = tagService.addTagToPost(tid, posts.get(0));
        tagService.deleteTagFromPost(tid, posts.get(0));
        // then
        assertThat(false).isEqualTo(postTagRepository.existsByPostAndTag(postRepository.findById(posts.get(0)).get(), tagRepository.findById(tid).get()));
    }

    @Test
    @DisplayName("포스트에 있는 태그 삭제 실패 테스트 - 잘못된 태그 아이디")
    public void deletePostTagTestToFailByTagId() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test = createTagDto("test");
        Long tid = tagService.createTag(test);

        // when
        Long tpid = tagService.addTagToPost(tid, posts.get(0));
        // then
        assertThrows(NotFoundByIdException.class, () -> tagService.deleteTagFromPost(tid + 1, posts.get(0)));
    }

    @Test
    @DisplayName("포스트에 있는 태그 삭제 실패 테스트 - 잘못된 포스트아이디")
    public void deletePostTagTestToFailByPostId() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test = createTagDto("test");
        Long tid = tagService.createTag(test);

        // when
        Long tpid = tagService.addTagToPost(tid, posts.get(0));
        // then
        assertThrows(NotFoundByIdException.class, () -> tagService.deleteTagFromPost(tid, posts.get(4)+1));
    }

    @Test
    @DisplayName("포스트에 있는 태그 삭제 실패 테스트 - 포스트 안에 없는 태그 삭제 요청")
    public void deletePostTagTestToFailByPostTag() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test = createTagDto("test");
        Long tid = tagService.createTag(test);

        // when
        Long tpid = tagService.addTagToPost(tid, posts.get(0));
        // then
        assertThrows(NotFoundPostTagException.class, () -> tagService.deleteTagFromPost(tid, posts.get(0)+1));
    }

    @Test
    @DisplayName("태그 삭제하는 테스트")
    public void deleteTagTest() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test = createTagDto("test");
        Long tid = tagService.createTag(test);

        // when
        Long tpid = tagService.addTagToPost(tid, posts.get(0));
        tagService.deleteTag(tid);

        // then
        assertThat(false).isEqualTo(postTagRepository.existsById(tpid));
        assertThat(0).isEqualTo(postTagRepository.findPostTagsByPost(postRepository.findById(posts.get(0)).get()).size());
    }

    @Test
    @DisplayName("태그 삭제 실패 테스트 - 잘못된 태그 아이디")
    public void deleteTagTestToFilByTagId() throws Exception{
        //given
        List<Long> users = createUsers(5);
        List<Long> categories = createCategories(5);
        List<Long> posts = createPosts(users, categories, 5);
        TagDto.SaveRequest test = createTagDto("test");
        Long tid = tagService.createTag(test);

        // when
        Long tpid = tagService.addTagToPost(tid, posts.get(0));
        // then
        assertThrows(NotFoundByIdException.class, () -> tagService.deleteTag(tid+1));
    }
}
