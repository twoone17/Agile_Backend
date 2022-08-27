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
import com.f3f.community.exception.commentException.NotFoundCommentException;
import com.f3f.community.exception.likeException.NotFoundLikesException;
import com.f3f.community.exception.postException.NotFoundPostByPostIdException;
import com.f3f.community.exception.scrapException.DuplicateScrapPostException;
import com.f3f.community.exception.scrapException.NotFoundScrapByIdException;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.likes.dto.LikesDto;
import com.f3f.community.likes.repository.LikesRepository;
import com.f3f.community.likes.service.LikesService;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.ScrapPost;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.repository.ScrapPostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.post.service.ScrapPostService;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.tag.domain.PostTag;
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
import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.f3f.community.common.constants.UserConstants.LIKE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.*;

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
            PostDto.SaveRequest postDto = createPostDto("title" + i, "content"+i ,userRepository.findById(users.get(random.nextInt(users.size()))).get(), categoryRepository.findById(categories.get(random.nextInt(categories.size()))).get());

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

    private UserDto.MyPageRequest createMyPageRequest(String userEmail, int limit, String option) {
        UserDto.MyPageRequest request = new UserDto.MyPageRequest(userEmail, limit, option);
        return request;
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

    private PostDto.SaveRequest createPostDto(String title,String content, User user, Category cat) {
        return PostDto.SaveRequest.builder()
                .title(title)
                .content(content)
                .author(user)
                .scrapList(new ArrayList<>())
                .likesList(new ArrayList<>())
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


    @BeforeEach
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

@org.junit.Test
    @DisplayName("공통 시나리오")
    public void commonScenario() throws Exception{
        //given
        UserDto.SaveRequest dongjae = createUser("류동재", "01012345678", "1234", "대림", "ryudd@gmail.com");
        UserDto.SaveRequest euisung = createUser("홍의성", "01012345678", "1234", "숭실대 입구", "euisungmanul@gmail.com");
        UserDto.SaveRequest cheolwoong = createUser("최철웅", "01012345678", "1234", "창원", "ironwoong@gmail.com");
        UserDto.SaveRequest yunjung = createUser("김윤정", "01012345678", "1234", "모란", "yjung@gmail.com");
        UserDto.SaveRequest dongjun = createUser("김동준", "01012345678", "1234", "정자", "djkim@gmail.com");

        Long ryu = userService.saveUser(dongjae);
        Long hong = userService.saveUser(euisung);
        Long choi = userService.saveUser(cheolwoong);
        Long yun = userService.saveUser(yunjung);
        Long jun = userService.saveUser(dongjun);

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

        PostDto.SaveRequest postDto1 = createPostDto("삼전 살만한가요?", "저 삼전 사고싶어용",userRepository.findById(ryu).get(), categoryRepository.findById(samsung).get());
        PostDto.SaveRequest postDto2 = createPostDto("애플은 안 망하네여", "하락률이 많이 낮아여",userRepository.findById(choi).get(), categoryRepository.findById(apple).get());
        PostDto.SaveRequest postDto3 = createPostDto("도지 화성가자!!!!!", "doge god",userRepository.findById(jun).get(), categoryRepository.findById(doge).get());
        PostDto.SaveRequest postDto4 = createPostDto("삼전 사고 싶다", "9만전자",userRepository.findById(yun).get(), categoryRepository.findById(samsung).get());
        PostDto.SaveRequest postDto5 = createPostDto("lg는 가전이지", "life is good",userRepository.findById(hong).get(), categoryRepository.findById(lg).get());
        PostDto.SaveRequest postDto6 = createPostDto("테슬라 와 도지?", "비트코인 전기 자동차",userRepository.findById(ryu).get(), categoryRepository.findById(tesla).get());
        PostDto.SaveRequest postDto7 = createPostDto("이더리움 폭락", "han river....",userRepository.findById(choi).get(), categoryRepository.findById(ethereum).get());
        PostDto.SaveRequest postDto8 = createPostDto("비트코인과 주식", "coin and stock",userRepository.findById(jun).get(), categoryRepository.findById(bitcoin).get());
        PostDto.SaveRequest postDto9 = createPostDto("아이폰 14 ", "비싸여",userRepository.findById(yun).get(), categoryRepository.findById(apple).get());
        PostDto.SaveRequest postDto10 = createPostDto("엘지 냉장고랑 티비", "비싸여",userRepository.findById(hong).get(), categoryRepository.findById(lg).get());

        Long post1 = postService.savePost(postDto1);
        Long post2 = postService.savePost(postDto2);
        Long post3 = postService.savePost(postDto3);
        Long post4 = postService.savePost(postDto4);
        Long post5 = postService.savePost(postDto5);
        Long post6 = postService.savePost(postDto6);
        Long post7 = postService.savePost(postDto7);
        Long post8 = postService.savePost(postDto8);
        Long post9 = postService.savePost(postDto9);
        Long post10 = postService.savePost(postDto10);
        // when
        TagDto.SaveRequest tagDto1 = createTagDto("주식초고수");
        Long tag1 = tagService.createTag(tagDto1);
        TagDto.SaveRequest tagDto2 = createTagDto("주식왕");
        Long tag2 = tagService.createTag(tagDto2);
        TagDto.SaveRequest tagDto3 = createTagDto("주린이");
        Long tag3 = tagService.createTag(tagDto3);
        TagDto.SaveRequest tagDto4 = createTagDto("재테크고수");
        Long tag4 = tagService.createTag(tagDto4);
        TagDto.SaveRequest tagDto5 = createTagDto("부자");
        Long tag5 = tagService.createTag(tagDto5);
        TagDto.SaveRequest tagDto6 = createTagDto("주식정보공유");
        Long tag6 = tagService.createTag(tagDto6);
        TagDto.SaveRequest tagDto7 = createTagDto("재테크초보");
        Long tag7 = tagService.createTag(tagDto7);
        TagDto.SaveRequest tagDto8 = createTagDto("재테크");
        Long tag8 = tagService.createTag(tagDto8);
        TagDto.SaveRequest tagDto9 = createTagDto("주식고수");
        Long tag9 = tagService.createTag(tagDto9);
        TagDto.SaveRequest tagDto10 = createTagDto("비트코인고수");
        Long tag10 = tagService.createTag(tagDto10);



        // when
        Long tpid1_post1 = tagService.addTagToPost(tag1, post1);
        Long tpid2_post1 = tagService.addTagToPost(tag2, post1);
        Long tpid3_post1 = tagService.addTagToPost(tag3, post1);
        Long tpid4_post1 = tagService.addTagToPost(tag4, post1);

        Long tpid1_post2 = tagService.addTagToPost(tag4, post2);
        Long tpid2_post2 = tagService.addTagToPost(tag2, post2);
        Long tpid3_post2 = tagService.addTagToPost(tag7, post2);

        Long tpid1_post3 = tagService.addTagToPost(tag10, post3);

        Long tpid1_post4 = tagService.addTagToPost(tag4, post4);
        Long tpid2_post4 = tagService.addTagToPost(tag3, post4);
        Long tpid3_post4 = tagService.addTagToPost(tag7, post4);

        Long tpid1_post5 = tagService.addTagToPost(tag6, post5);

        Long tpid1_post6 = tagService.addTagToPost(tag1, post6);
        Long tpid2_post6 = tagService.addTagToPost(tag10, post6);

        Long tpid1_post7 = tagService.addTagToPost(tag3, post7);
        Long tpid2_post7 = tagService.addTagToPost(tag7, post7);
        Long tpid3_post7 = tagService.addTagToPost(tag2, post7);
        Long tpid4_post7 = tagService.addTagToPost(tag9, post7);

//      Long tpid1_post8 = tagService.addTagToPost(tag3, post8);

        Long tpid1_post9 = tagService.addTagToPost(tag2, post9);
        Long tpid2_post9 = tagService.addTagToPost(tag8, post9);

        Long tpid1_post10 = tagService.addTagToPost(tag5, post10);

        // then
        CommentDto.SaveRequest commentDto1 = createCommentDto(post1,null , yun, "삼성 주식 지금 얼만데요?");
        Long commentsId1 = commentService.createComments(commentDto1);
        Comment comment1 = commentRepository.findById(commentsId1).get();

        CommentDto.SaveRequest commentDto2 = createCommentDto(post1,comment1.getId(), choi, "비싸요");
        Long commentsId2 = commentService.createComments(commentDto2);
        Comment comment2 = commentRepository.findById(commentsId2).get();

        CommentDto.SaveRequest commentDto3 = createCommentDto(post2, null, ryu, "애플이 짱이지");
        Long commentsId3 = commentService.createComments(commentDto3);
        Comment comment3 = commentRepository.findById(commentsId3).get();

        CommentDto.SaveRequest commentDto4 = createCommentDto(post3,null , hong, "번아웃");
        Long commentsId4 = commentService.createComments(commentDto4);
        Comment comment4 = commentRepository.findById(commentsId4).get();

        CommentDto.SaveRequest commentDto5 = createCommentDto(post3,comment4.getId(), choi, "또?");
        Long commentsId5 = commentService.createComments(commentDto5);
        Comment comment5 = commentRepository.findById(commentsId5).get();

        CommentDto.SaveRequest commentDto6 = createCommentDto(post3,comment4.getId(), yun, "나도 번아웃,,,");
        Long commentsId6 = commentService.createComments(commentDto6);
        Comment comment6 = commentRepository.findById(commentsId6).get();
        CommentDto.SaveRequest commentDto7 = createCommentDto(post5,null , choi, "바쁘다바빠");
        Long commentsId7 = commentService.createComments(commentDto7);
        Comment comment7 = commentRepository.findById(commentsId7).get();

        CommentDto.SaveRequest commentDto8 = createCommentDto(post10,null, yun, "아무것도 몰라요");
        Long commentsId8 = commentService.createComments(commentDto8);
        Comment comment8 = commentRepository.findById(commentsId8).get();

        CommentDto.SaveRequest commentDto9 = createCommentDto(post10,comment8.getId(), ryu, "아무것도 모르면 안되는데");
        Long commentsId9 = commentService.createComments(commentDto9);
        Comment comment9 = commentRepository.findById(commentsId9).get();

        CommentDto.SaveRequest commentDto10 = createCommentDto(post10, null, jun, "하루는 24시간");
        Long commentsId10 = commentService.createComments(commentDto10);
        Comment comment10 = commentRepository.findById(commentsId10).get();

        LikesDto.SaveRequest likesDto1 = createLikesDto(post1, yun);
        Long likesId1 = likesService.createLikes(likesDto1);
        Likes likes1 = likesRepository.findById(likesId1).get();

        LikesDto.SaveRequest likesDto2 = createLikesDto(post1, ryu);
        Long likesId2 = likesService.createLikes(likesDto2);
        Likes likes2 = likesRepository.findById(likesId2).get();

        LikesDto.SaveRequest likesDto3 = createLikesDto(post2, yun);
        Long likesId3 = likesService.createLikes(likesDto3);
        Likes likes3 = likesRepository.findById(likesId3).get();

        LikesDto.SaveRequest likesDto4 = createLikesDto(post3, jun);
        Long likesId4 = likesService.createLikes(likesDto4);
        Likes likes4 = likesRepository.findById(likesId4).get();

        LikesDto.SaveRequest likesDto5 = createLikesDto(post4, hong);
        Long likesId5 = likesService.createLikes(likesDto5);
        Likes likes5 = likesRepository.findById(likesId5).get();

        LikesDto.SaveRequest likesDto6 = createLikesDto(post6, hong);
        Long likesId6 = likesService.createLikes(likesDto6);
        Likes likes6 = likesRepository.findById(likesId6).get();

        LikesDto.SaveRequest likesDto7 = createLikesDto(post6, choi);
        Long likesId7 = likesService.createLikes(likesDto7);
        Likes likes7 = likesRepository.findById(likesId7).get();

        LikesDto.SaveRequest likesDto8 = createLikesDto(post8, choi);
        Long likesId8 = likesService.createLikes(likesDto8);
        Likes likes8 = likesRepository.findById(likesId8).get();

        LikesDto.SaveRequest likesDto9 = createLikesDto(post8, ryu);
        Long likesId9 = likesService.createLikes(likesDto9);
        Likes likes9 = likesRepository.findById(likesId9).get();

        LikesDto.SaveRequest likesDto10 = createLikesDto(post10, jun);
        Long likesId10 = likesService.createLikes(likesDto10);
        Likes likes10 = likesRepository.findById(likesId10).get();


        ScrapDto.SaveRequest scrap1Dto = createScrapDto(userRepository.findById(ryu).get(), "경제");
        ScrapDto.SaveRequest scrap2Dto = createScrapDto(userRepository.findById(jun).get(), "할래");
        ScrapDto.SaveRequest scrap3Dto = createScrapDto(userRepository.findById(yun).get(), "대로");
        ScrapDto.SaveRequest scrap4Dto = createScrapDto(userRepository.findById(choi).get(), "내맘");
        ScrapDto.SaveRequest scrap5Dto = createScrapDto(userRepository.findById(hong).get(), "주식");
        ScrapDto.SaveRequest scrap6Dto = createScrapDto(userRepository.findById(jun).get(), "경제");
        ScrapDto.SaveRequest scrap7Dto = createScrapDto(userRepository.findById(choi).get(), "도지");
        ScrapDto.SaveRequest scrap8Dto = createScrapDto(userRepository.findById(yun).get(), "coin");
        ScrapDto.SaveRequest scrap9Dto = createScrapDto(userRepository.findById(hong).get(), "rastle");
        ScrapDto.SaveRequest scrap10Dto = createScrapDto(userRepository.findById(jun).get(), "coin");

        Long scrap1 = scrapService.createScrap(scrap1Dto);
        Long scrap2 = scrapService.createScrap(scrap2Dto);
        Long scrap3 = scrapService.createScrap(scrap3Dto);
        Long scrap4 = scrapService.createScrap(scrap4Dto);
        Long scrap5 = scrapService.createScrap(scrap5Dto);
        Long scrap6 = scrapService.createScrap(scrap6Dto);
        Long scrap7 = scrapService.createScrap(scrap7Dto);
        Long scrap8 = scrapService.createScrap(scrap8Dto);
        Long scrap9 = scrapService.createScrap(scrap9Dto);
        Long scrap10 = scrapService.createScrap(scrap10Dto);

        scrapService.saveCollection(scrap1, ryu, post1);
        scrapService.saveCollection(scrap1, ryu, post8);
        scrapService.saveCollection(scrap1, ryu, post9);
        scrapService.saveCollection(scrap2, jun, post2);
        scrapService.saveCollection(scrap2, jun, post4);
        scrapService.saveCollection(scrap6, jun, post3);
        scrapService.saveCollection(scrap4, choi, post5);
        scrapService.saveCollection(scrap4, choi, post7);
        scrapService.saveCollection(scrap7, choi, post9);
        scrapService.saveCollection(scrap5, hong, post10);
        scrapService.saveCollection(scrap5, hong, post1);
        scrapService.saveCollection(scrap9, hong, post5);
        scrapService.saveCollection(scrap3, yun, post6);
        scrapService.saveCollection(scrap3, yun, post2);
        scrapService.saveCollection(scrap8, yun, post3);
        scrapService.saveCollection(scrap8, yun, post4);
        scrapService.saveCollection(scrap10, jun, post10);
        scrapService.saveCollection(scrap7, choi, post7);
        scrapService.saveCollection(scrap9, hong, post6);

        //전체 포스트는 10개이다
        assertThat(postRepository.findAll().size()).isEqualTo(10);

        //전체 포스트의 title과 content 검증
        assertThat(postRepository.findAll())
            .extracting("title", "content")
            .contains(tuple("삼전 살만한가요?", "저 삼전 사고싶어용"),
                    tuple("애플은 안 망하네여", "하락률이 많이 낮아여"),
                    tuple("도지 화성가자!!!!!", "doge god"),
                    tuple("삼전 사고 싶다", "9만전자"),
                    tuple("lg는 가전이지", "life is good"),
                    tuple("테슬라 와 도지?", "비트코인 전기 자동차"),
                    tuple("이더리움 폭락", "han river...."),
                    tuple("비트코인과 주식", "coin and stock"),
                    tuple("아이폰 14 ", "비싸여"),
                    tuple("엘지 냉장고랑 티비", "비싸여"));
//            .doesNotContain();
        //포스트 삭제
//        post1, post3 , post4, post7
        postService.deletePost(post1,ryu);
        postService.deletePost(post3,jun);
        postService.deletePost(post4,yun);
        postService.deletePost(post7,choi);

        //id값으로 검증
        assertThat(postRepository.existsById(post1)).isFalse();
        assertThat(postRepository.existsById(post3)).isFalse();
        assertThat(postRepository.existsById(post4)).isFalse();
        assertThat(postRepository.existsById(post7)).isFalse();

        //실제 값 검증
        assertThat(postRepository.findAll())
            .extracting("title", "content")
            .contains(
                    tuple("애플은 안 망하네여", "하락률이 많이 낮아여"),
                    tuple("lg는 가전이지", "life is good"),
                    tuple("테슬라 와 도지?", "비트코인 전기 자동차"),
                    tuple("비트코인과 주식", "coin and stock"),
                    tuple("아이폰 14 ", "비싸여"),
                    tuple("엘지 냉장고랑 티비", "비싸여"))
                .doesNotContain(tuple("삼전 살만한가요?", "저 삼전 사고싶어용"),
                        tuple("도지 화성가자!!!!!", "doge god"),
                        tuple("삼전 사고 싶다", "9만전자"),
                        tuple("이더리움 폭락", "han river...."));

        //사이즈 검증
        assertThat(postRepository.findAll().size()).isEqualTo(6);

        //포스트 삭제시 좋아요 삭제 여부 확인
        assertThrows(NotFoundLikesException.class, () -> likesRepository.findById(likesId1).orElseThrow(NotFoundLikesException::new));
        assertThrows(NotFoundLikesException.class, () -> likesRepository.findById(likesId2).orElseThrow(NotFoundLikesException::new));
        assertThrows(NotFoundLikesException.class, () -> likesRepository.findById(likesId4).orElseThrow(NotFoundLikesException::new));
        assertThrows(NotFoundLikesException.class, () -> likesRepository.findById(likesId5).orElseThrow(NotFoundLikesException::new));

        //포스트 삭제시 댓글 삭제 여부 확인 1,2,4,5,6
        assertThrows(NotFoundCommentException.class, () -> commentService.findCommentById(comment1.getId()));
        assertThrows(NotFoundCommentException.class, () -> commentService.findCommentById(comment2.getId()));
        assertThrows(NotFoundCommentException.class, () -> commentService.findCommentById(comment4.getId()));
        assertThrows(NotFoundCommentException.class, () -> commentService.findCommentById(comment5.getId()));
        assertThrows(NotFoundCommentException.class, () -> commentService.findCommentById(comment6.getId()));

        //포스트 삭제시 스크랩 삭제 여부 확인


        /*
        유저
        벤 - 윤정이
        삭제 - 동재

        포스트 삭제
        post1, post4, post7

        댓글 삭제
        comment 5, comment 8

        카테고리 삭제
        도지

        태그 삭제
        제테크, 제테크 초보

        스크랩 삭제
        scrap 10,9

         */
//        assertThat(postRepository.findAll().size()).isEqualTo(10);
//
//        assertThat(4).isEqualTo(postTagRepository.findPostTagsByPost(postRepository.findById(post1).get()).size());
//        List<PostTag> postTagsByPost = postTagRepository.findPostTagsByPost(postRepository.findById(post1).get());
//
//        assertThat(postTagRepository.findPostTagsByPost(postRepository.findById(post1).get()).contains("주식초고수"));
//        //가져온 포스트에 카테고리, 태그 잘 연결되어있는지
////        assertThat(tpid).isEqualTo(postTagRepository.findByPostAndTag(postRepository.findById(posts.get(0)).get(), tagRepository.findById(tid).get()).get().getId());
//





    }



}
