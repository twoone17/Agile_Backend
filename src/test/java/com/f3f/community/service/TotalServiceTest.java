package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.comment.domain.Comment;
import com.f3f.community.comment.dto.CommentDto;
import com.f3f.community.comment.repository.CommentRepository;
import com.f3f.community.comment.service.CommentService;
import com.f3f.community.exception.categoryException.MaxDepthException;
import com.f3f.community.exception.scrapException.DuplicateScrapPostException;
import com.f3f.community.likes.dto.LikesDto;
import com.f3f.community.likes.repository.LikesRepository;
import com.f3f.community.likes.service.LikesService;
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
import com.f3f.community.user.domain.UserLevel;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TotalServiceTest {
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

    @Autowired
    LikesService likesService;

    @Autowired
    LikesRepository likesRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;


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

    private List<Long> createTags(int n, List<Long> posts) throws Exception {
        List<Long> tids = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            TagDto.SaveRequest tagDto = createTagDto("tag" + i);
            Long tid = tagService.createTag(tagDto);
            tids.add(tid);
            for (int j = 0; j < 6; j++) {
                try {
                    tagService.addTagToPost(tid, posts.get(random.nextInt(posts.size())));
                } catch (Exception e) {
                    System.out.println("e = " + e);
                }
            }
        }
        return tids;
    }

    private List<Long> createComments(int n, List<Long> posts, List<Long> users) {
        List<Long> comments = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            Long pid = posts.get(random.nextInt(posts.size()));
            CommentDto.SaveRequest commentDto;
            if (commentRepository.findByPostId(pid).isEmpty()) {
                 commentDto= createCommentDto(pid, null, users.get(random.nextInt(users.size())), "comment" + i);
            } else {
                List<Comment> byPostId = commentRepository.findByPostId(pid);
                commentDto = createCommentDto(pid, byPostId.get(random.nextInt(byPostId.size())).getId(), users.get(random.nextInt(users.size())), "comment" + i);
            }
            try {
                Long comment = commentService.createComments(commentDto);
                comments.add(comment);
            } catch (Exception e) {
                System.out.println("e = " + e);
            }
        }
        return comments;
    }

    private List<Long> createLikes(int n, List<Long> posts, List<Long> users) {
        List<Long> likes = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            LikesDto.SaveRequest likesDto = createLikesDto(posts.get(random.nextInt(posts.size())), users.get(random.nextInt(users.size())));
            try {
                Long likes1 = likesService.createLikes(likesDto);
                likes.add(likes1);
            } catch (Exception e) {
                System.out.println("e = " + e);
            }


        }
        return likes;
    }

    private UserDto.SaveRequest createUserDto(String name) {
        return UserDto.SaveRequest.builder()
                .email(name + "@" + name + ".com")
                .userGrade(UserGrade.GOLD)
                .phone("010123457678")
                .nickname("nick" + name)
                .password("a12345678@")
                .userLevel(UserLevel.UNBAN)
                .address("gachon univ")
                .build();
    }

    private UserDto.SaveRequest createUser(String nickname, String phone, String password, String address, String email) {
        return UserDto.SaveRequest.builder()
                .email(email)
                .userGrade(UserGrade.GOLD)
                .phone(phone)
                .nickname(nickname)
                .password(password)
                .userLevel(UserLevel.UNBAN)
                .address(address)
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

    private CommentDto.SaveRequest createCommentDto(Long pid, Long cid, Long uid, String content) {
        if (cid == null) {
            return CommentDto.SaveRequest.builder()
                    .post(postRepository.findById(pid).get())
                    .author(userRepository.findById(uid).get())
                    .parentComment(null)
                    .childComment(new ArrayList<>())
                    .content(content)
                    .depth(0L)
                    .build();
        }else{
            return CommentDto.SaveRequest.builder()
                    .post(postRepository.findById(pid).get())
                    .author(userRepository.findById(uid).get())
                    .parentComment(commentRepository.findById(cid).get())
                    .childComment(new ArrayList<>())
                    .content(content)
                    .depth(1L)
                    .build();
        }

    }

    private LikesDto.SaveRequest createLikesDto(Long pid, Long uid) {
        return LikesDto.SaveRequest.builder()
                .user(userRepository.findById(uid).get())
                .post(postRepository.findById(pid).get()).build();
    }


    @Before
    public void deleteAll() {
        postTagRepository.deleteAll();
        tagRepository.deleteAll();
        scrapPostRepository.deleteAll();
        scrapRepository.deleteAll();
        likesRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("객체들 자동 생성 테스트")
    public void createAutomationTest() throws Exception {
        //given
        List<Long> users = createUsers(30);
        List<Long> categories = createCategories(50);
        List<Long> posts = createPosts(users, categories, 60);
        List<Long> scraps = createScraps(users, posts, 15);
        List<Long> tags = createTags(14, posts);
        List<Long> comments = createComments(50, posts, users);
        List<Long> likes = createLikes(60, posts, users);
        // when

        // then

    }

    @Test
    @DisplayName("공통 시나리오")
    public void commonScenario() throws Exception{
        //given
        UserDto.SaveRequest dongjae = createUser("류동재", "01012345678", "1234", "대림", "ryudd@gmail.com");
        UserDto.SaveRequest euisung = createUser("홍의성", "01012345678", "1234", "숭실대 입구", "euisungmanul@gmail.com");
        UserDto.SaveRequest cheolwoong = createUser("최철웅", "01012345678", "1234", "창원", "ironwoong@gmail.com");
        UserDto.SaveRequest yunjung = createUser("김윤정", "01012345678", "1234", "모란", "yjung@gmail.com");
        UserDto.SaveRequest dongjun = createUser("김동준", "01012345678", "1234", "정자", "djkim@gmail.com");

        Long root = createRoot();
        CategoryDto.SaveRequest stockDto = createCategoryDto("주식", categoryRepository.findById(root).get());
        CategoryDto.SaveRequest bitcoinDto = createCategoryDto("비트코인", categoryRepository.findById(root).get());
        Long stock = categoryService.createCategory(stockDto);
        Long bitcoin = categoryService.createCategory(bitcoinDto);

        CategoryDto.SaveRequest kospiDto = createCategoryDto("코스피", categoryRepository.findById(stock).get());
        CategoryDto.SaveRequest nasdaqDto = createCategoryDto("나스닥", categoryRepository.findById(stock).get());
        Long kospi = categoryService.createCategory(kospiDto);
        Long nasdaq = categoryService.createCategory(nasdaqDto);

        CategoryDto.SaveRequest samsungDto = createCategoryDto("삼전", categoryRepository.findById(kospi).get());
        CategoryDto.SaveRequest lgDto = createCategoryDto("엘지", categoryRepository.findById(kospi).get());
        CategoryDto.SaveRequest appleDto = createCategoryDto("애플", categoryRepository.findById(nasdaq).get());
        CategoryDto.SaveRequest teslaDto = createCategoryDto("테슬라", categoryRepository.findById(nasdaq).get());
        CategoryDto.SaveRequest ethereumDto = createCategoryDto("이더리움", categoryRepository.findById(bitcoin).get());
        CategoryDto.SaveRequest dogeDto = createCategoryDto("도지", categoryRepository.findById(bitcoin).get());
        Long samsung = categoryService.createCategory(samsungDto);
        Long lg = categoryService.createCategory(lgDto);
        Long apple = categoryService.createCategory(appleDto);
        Long tesla = categoryService.createCategory(teslaDto);
        Long ethereum = categoryService.createCategory(ethereumDto);
        Long doge = categoryService.createCategory(dogeDto);
        // when

        // then
    }
}
