package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.likes.dto.LikesDto;
import com.f3f.community.likes.repository.LikesRepository;
import com.f3f.community.likes.service.LikesService;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.repository.ScrapPostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.domain.UserLevel;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.f3f.community.common.constants.UserConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
//@Transactional
class LikesServiceTest {

    @Autowired
    LikesRepository likesRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    ScrapRepository scrapRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScrapPostRepository scrapPostRepository;
    @Autowired
    PostService postService;
    @Autowired
    ScrapService scrapService;
    @Autowired
    LikesService likesService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;

    @BeforeEach
    public void deleteAll() {
        scrapPostRepository.deleteAll();
        scrapRepository.deleteAll();
        postRepository.deleteAll();
        likesRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    private LikesDto createLikesDto(User user, Post post) {
        return new LikesDto(user, post);
    }

    private UserDto.SaveRequest createUser() {
        UserDto.SaveRequest userInfo = new UserDto.SaveRequest("tempabc@tempabc.com", "ppadb123@", "01098745632", UserGrade.BRONZE, UserLevel.UNBAN, "brandy", "pazu");
        return userInfo;
    }


    private PostDto.SaveRequest createPostDto(User user, Category cat, int index) {
        return PostDto.SaveRequest.builder()
                .title("test title" + index)
                .content("test content for test" + index)
                .author(user)
                .viewCount(index)
                .scrapList(new ArrayList<>())
                .likesList(new ArrayList<>())
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

    private Category createRoot() throws Exception{
        CategoryDto.SaveRequest cat = createCategoryDto("root", null);
        Long rid = categoryService.createCategory(cat);
        return categoryRepository.findById(rid).get();
    }

    @Test
    @DisplayName("포스트 좋아요 테스트")
    public void createLikesTest() throws Exception {
        //given
        UserDto.SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> byId = userRepository.findById(aLong);
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest postDto1 = createPostDto(byId.get(), cat, 1);
        Long aLong1 = postService.savePost(postDto1);

        //when
        Optional<Post> byIdPost = postRepository.findById(aLong1);
        LikesDto likesDto = createLikesDto(byId.get(), byIdPost.get());
        Long likesId = likesService.createLikes(likesDto);
        Optional<Likes> byId1 = likesRepository.findById(likesId);

        //then
        assertThat(byId1.get().getId()).isEqualTo(likesId);
    }

    @Test
    @DisplayName("좋아요 유저 자동등록 테스트")
    public void addToUserLikesListTest() throws Exception {
        //given
        UserDto.SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> byId = userRepository.findById(aLong);
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest postDto1 = createPostDto(byId.get(), cat, 1);
        Long aLong1 = postService.savePost(postDto1);

        //when
        Optional<Post> byIdPost = postRepository.findById(aLong1);
        LikesDto likesDto = createLikesDto(byId.get(), byIdPost.get());
        Long likesId = likesService.createLikes(likesDto);
        Optional<Likes> userLikesList = likesRepository.findById(byId.get().getId());
        List<Likes> byUserId = likesRepository.findByUser(byId.get());

        //then
        assertThat(byUserId.size()).isEqualTo(1);
    }
}