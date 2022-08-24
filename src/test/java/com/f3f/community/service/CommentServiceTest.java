package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.comment.domain.Comment;
import com.f3f.community.comment.dto.CommentDto;
import com.f3f.community.comment.repository.CommentRepository;
import com.f3f.community.comment.service.CommentService;
import com.f3f.community.likes.repository.LikesRepository;
import com.f3f.community.media.domain.Media;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.repository.ScrapPostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.domain.UserLevel;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.f3f.community.comment.dto.CommentDto.UpdateCommentRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
@RunWith(SpringRunner.class)
@SpringBootTest
class CommentServiceTest {
    @Autowired CommentRepository commentRepository;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired LikesRepository likesRepository;
    @Autowired ScrapPostRepository scrapPostRepository;
    @Autowired ScrapRepository scrapRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired UserService userService;
    @Autowired CommentService commentService;
    @Autowired PostService postService;
    @Autowired CategoryService categoryService;

    @BeforeEach
    public void deleteAll() {
        scrapPostRepository.deleteAll();
        scrapRepository.deleteAll();
        likesRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    private CommentDto.SaveRequest createCommentDto(User user, Post post, String content, Comment parent, List<Comment> child, Long depth, List<Media> mediaList) {
        CommentDto.SaveRequest saveRequest = new CommentDto.SaveRequest(content, post, user, parent, child, depth, mediaList);
        return saveRequest;
    }

    private CommentDto.SaveRequest deleteCommentDto(User user, Post post, String content, Comment parent, List<Comment> child, Long depth, List<Media> mediaList) {
        CommentDto.SaveRequest saveRequest = new CommentDto.SaveRequest(content, post, user, parent, child, depth, mediaList);
        return saveRequest;
    }

    private UserDto.SaveRequest createUserWithUniqueCount(int i) {
        UserDto.SaveRequest userInfo = new UserDto.SaveRequest("tempabc"+ i +"@tempabc.com", "ppadb123@" + i, "0109874563" + i, UserGrade.BRONZE, UserLevel.UNBAN, "brandy" + i, "pazu");
//        User user = userInfo.toEntity();
        return userInfo;
    }

    private PostDto.SaveRequest createPostDto(User user, Category cat, int index) {
        return PostDto.SaveRequest.builder()
                .title("test title" + index)
                .content("test content for test" + index)
                .author(user)
                .scrapList(new ArrayList<>())
                .commentList(new ArrayList<>())
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
    @DisplayName("댓글 생성 테스트")
    public void saveCommentTest() throws Exception {
        //given
        UserDto.SaveRequest userDTO = createUserWithUniqueCount(1);
        Long aLong = userService.saveUser(userDTO);
        User user = userRepository.findById(aLong).get();
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest postDto = createPostDto(user, cat, 1);
        Long aLong1 = postService.savePost(postDto);
        Post post = postRepository.findById(aLong1).get();

        //when
        CommentDto.SaveRequest commentDto = createCommentDto(user, post, "Content", null, new ArrayList<>(), 1L, new ArrayList<>());
        Long commentsId = commentService.createComments(commentDto);
        Comment comment = commentRepository.findById(commentsId).get();

        //then
        assertThat(comment.getContent()).isEqualTo("Content");
        //assertThat(post.getCommentList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    public void updateCommentTest() throws Exception {
        //given
        UserDto.SaveRequest userDTO = createUserWithUniqueCount(1);
        Long aLong = userService.saveUser(userDTO);
        User user = userRepository.findById(aLong).get();
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest postDto = createPostDto(user, cat, 1);
        Long aLong1 = postService.savePost(postDto);
        Post post = postRepository.findById(aLong1).get();
        CommentDto.SaveRequest commentDto = createCommentDto(user, post, "Content", null, new ArrayList<>(), 1L, new ArrayList<>());
        Long commentsId = commentService.createComments(commentDto);
        Comment comment = commentRepository.findById(commentsId).get();

        //when
        String changedContent = "Changed-Content";
        UpdateCommentRequest updateRequest = new UpdateCommentRequest(user.getEmail(), post.getId(), comment.getId(), "Content", changedContent);
        commentService.updateComment(updateRequest);
        // 다시 찾아오기
        Comment changedComment = commentRepository.findById(comment.getId()).get();

        //then
        assertThat(changedComment.getContent()).isEqualTo(changedContent);
    }



    @Test
    @DisplayName("댓글 삭제 테스트 - 부모")
    public void deleteCommentTest() throws Exception {
        //given
        UserDto.SaveRequest userDTO = createUserWithUniqueCount(1);
        Long aLong = userService.saveUser(userDTO);
        User user = userRepository.findById(aLong).get();

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        PostDto.SaveRequest postDto = createPostDto(user, cat, 1);
        Long aLong1 = postService.savePost(postDto);
        Post post = postRepository.findById(aLong1).get();

        CommentDto.SaveRequest commentDto1 = createCommentDto(user, post, "Content1", null, new ArrayList<>(), 1L, new ArrayList<>());
        Long commentsId1 = commentService.createComments(commentDto1);
        Comment comment1 = commentRepository.findById(commentsId1).get();
        CommentDto.SaveRequest commentDto2 = createCommentDto(user, post, "Content2", null, new ArrayList<>(), 1L, new ArrayList<>());
        Long commentsId2 = commentService.createComments(commentDto2);
        Comment comment2 = commentRepository.findById(commentsId2).get();

        //when
        commentService.deleteComments(commentsId1);
        //then
        List<Comment> byPost = commentRepository.findByPost(post);
        assertThat(commentRepository.existsById(commentsId1)).isEqualTo(false);
        assertThat(byPost.size()).isEqualTo(1);
    }
}