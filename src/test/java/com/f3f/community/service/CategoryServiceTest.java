package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    private UserDto.SaveRequest createUserDto1(){
        return new UserDto.SaveRequest("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
    }

    private UserDto.SaveRequest createUserDto2(){
        return new UserDto.SaveRequest("temp2@temp2.com", "1234567", "01012341234",
                UserGrade.BRONZE, "own", "seoul");
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


    @Test
    @DisplayName("depth가 제대로 들어가는지 확인하는 테스트")
    public void depthInsertionTest() throws Exception{
        //given
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", null);

        // when
        Long id = categoryService.createCategory(categoryDto);


        // then
        assertThat(0L).isEqualTo(categoryRepository.findById(id).get().getDepth());
    }

    @Test
    @DisplayName("자녀 카테고리 depth 확인 테스트")
    public void childDepthInsertionTest() throws Exception{
        //given
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", null);
        Long id1 = categoryService.createCategory(cat1);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(id1).get());
        // when
        Long id2 = categoryService.createCategory(cat2);
        // then
        assertThat(1L).isEqualTo(categoryRepository.findById(id2).get().getDepth());
    }

    @Test
    @DisplayName("자녀의 포스트까지 가져오는지 확인하는 테스트")
    public void getPostsTest() throws Exception{
        //given
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", null);
        Long id1 = categoryService.createCategory(cat1);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(id1).get());
        Long id2 = categoryService.createCategory(cat2);
        User user = createUserDto1().toEntity();
        PostDto.SaveRequest post1 = createPostDto1(user, categoryRepository.findById(id1).get());
        PostDto.SaveRequest post2 = createPostDto2(user, categoryRepository.findById(id2).get());
        Long uid = userService.saveUser(user);
        Long pid1 = postService.SavePost(post1);
        Long pid2 = postService.SavePost(post2);

        // when
        List<Post> posts = categoryService.getPosts(id1);
        // then
        assertThat(2).isEqualTo(posts.size());
        for (Post post : posts) {
            System.out.println(post.getTitle());
        }
    }


    @Test
    @DisplayName("삭제 테스트 - 자녀까지 지워지는지")
    public void deleteTest() throws Exception{
        //given
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", null);
        Long id1 = categoryService.createCategory(cat1);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(id1).get());
        Long id2 = categoryService.createCategory(cat2);

        // when
        categoryService.deleteCategory(id1);
        // then
        assertThat(false).isEqualTo(categoryRepository.existsById(id2));
    }
}
