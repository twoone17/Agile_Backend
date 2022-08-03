package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.categoryException.*;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
                UserGrade.BRONZE, "james", "changwon", false);
    }

    private UserDto.SaveRequest createUserDto2(){
        return new UserDto.SaveRequest("temp2@temp2.com", "1234567", "01012341234",
                UserGrade.BRONZE, "own", "seoul", false);
    }

    private PostDto.SaveRequest createPostDto(String title, User user, Category cat) {
        return PostDto.SaveRequest.builder()
                .title(title)
                .content("temp content")
                .author(user)
                .scrapList(new ArrayList<>())
                .category(cat).build();
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

    private Category createRoot() throws Exception{
        CategoryDto.SaveRequest cat = createCategoryDto("root", null);
        Long rid = categoryService.createCategory(cat);
        return categoryRepository.findById(rid).get();
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
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);

        // when
        Long id = categoryService.createCategory(categoryDto);


        // then
        assertThat(1L).isEqualTo(categoryRepository.findById(id).get().getDepth());
    }

    @Test
    @DisplayName("자녀 카테고리 depth 확인 테스트")
    public void childDepthInsertionTest() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", root);
        Long id1 = categoryService.createCategory(cat1);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(id1).get());
        // when
        Long id2 = categoryService.createCategory(cat2);
        // then
        assertThat(2L).isEqualTo(categoryRepository.findById(id2).get().getDepth());
    }

    @Test
    @DisplayName("null name으로 카테고리 생성 실패 테스트")
    public void createCategoryTestToFailByNullName() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto(null, root);
        // then
        assertThrows(NotFoundCategoryNameException.class, () -> categoryService.createCategory(cat1));
    }

    @Test
    @DisplayName("empty name으로 카테고리 생성 실패 테스트")
    public void createCategoryTestToFailByEmptyName() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("", root);

        // then
        assertThrows(NotFoundCategoryNameException.class, () -> categoryService.createCategory(cat1));
    }

    @Test
    @DisplayName("postList == null 로 인한 카테고리 생성 실패 테스트")
    public void createCategoryTestToFailByEmptyPostList() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = CategoryDto.SaveRequest.builder()
                .categoryName("temp")
                .parents(root)
                .childCategory(new ArrayList<>()).build();

        // then
        assertThrows(NotFoundCategoryPostListException.class, () -> categoryService.createCategory(cat1));
    }

    @Test
    @DisplayName("child category list == null 로 인한 카테고리 생성 실패 테스트")
    public void createCategoryTestToFailByEmptyChildCategory() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = CategoryDto.SaveRequest.builder()
                .categoryName("temp")
                .parents(root)
                .postList(new ArrayList<>()).build();

        // then
        assertThrows(NotFoundChildCategoryListException.class, () -> categoryService.createCategory(cat1));
    }

    @Test
    @DisplayName("부모 카테고리가 Null 로 인한 카테고리 생성 실패 테스트")
    public void createCategoryTestToFailByEmptyParentCategory() throws Exception {
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = CategoryDto.SaveRequest.builder()
                .categoryName("temp")
                .parents(null)
                .postList(new ArrayList<>())
                .childCategory(new ArrayList<>()).build();

        // then
        assertThrows(NotFoundParentCategoryException.class, () -> categoryService.createCategory(cat1));
    }


    @Test
    @DisplayName("자녀의 포스트까지 가져오는지 확인하는 테스트 - 최종 depth 1")
    public void getPostsTestOneChildOneDepth() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", root);
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
    @DisplayName("자녀의 자녀 포스트까지 가져오는지 확인하는 테스트 - 최종 depth 2")
    public void getPostTestThreeChildMaxDepth() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", root);
        Long id1 = categoryService.createCategory(cat1);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(id1).get());
        Long id2 = categoryService.createCategory(cat2);
        CategoryDto.SaveRequest cat3 = createCategoryDto("temp3", categoryRepository.findById(id2).get());
        Long id3 = categoryService.createCategory(cat3);
        CategoryDto.SaveRequest cat4 = createCategoryDto("temp4", categoryRepository.findById(id2).get());
        Long id4 = categoryService.createCategory(cat4);
        User user = createUserDto1().toEntity();
        userService.saveUser(user);
        PostDto.SaveRequest post1 = createPostDto("title1", user, categoryRepository.findById(id1).get());
        PostDto.SaveRequest post2 = createPostDto("title2", user, categoryRepository.findById(id1).get());
        PostDto.SaveRequest post3 = createPostDto("title3", user, categoryRepository.findById(id2).get());
        PostDto.SaveRequest post4 = createPostDto("title4", user, categoryRepository.findById(id3).get());
        PostDto.SaveRequest post5 = createPostDto("title5", user, categoryRepository.findById(id4).get());
        PostDto.SaveRequest post6 = createPostDto("title6", user, categoryRepository.findById(id4).get());
        postService.SavePost(post1);
        postService.SavePost(post2);
        postService.SavePost(post3);
        postService.SavePost(post4);
        postService.SavePost(post5);
        postService.SavePost(post6);

        // when
        List<Post> posts = categoryService.getPosts(id1);
        // then
        assertThat(6).isEqualTo(posts.size());
        for (Post post : posts) {
            System.out.println(post.getTitle());
        }
    }

    @Test
    @DisplayName("이름변경 실패 테스트 - 중복된 이름")
    public void updateNameTestToFailByDuplicateName() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", root);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", root);

        // when
        Long cid1 = categoryService.createCategory(cat1);
        Long cid2 = categoryService.createCategory(cat2);

        // then
        assertThrows(DuplicateCategoryNameException.class, () -> categoryService.updateCategoryName(cid1, "temp2"));
    }

    @Test
    @DisplayName("삭제 테스트 - 자녀까지 지워지는지")
    public void deleteTestOneChildOneDepth() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", root);
        Long id1 = categoryService.createCategory(cat1);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(id1).get());
        Long id2 = categoryService.createCategory(cat2);

        // when
        categoryService.deleteCategory(id1);
        // then
        assertThat(false).isEqualTo(categoryRepository.existsById(id2));
    }

    @Test
    @DisplayName("삭제 테스트 - 다수의 자녀도 지워지는지")
    public void deleteTestMultiChildMaxDepth() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", root);
        Long id1 = categoryService.createCategory(cat1);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(id1).get());
        Long id2 = categoryService.createCategory(cat2);
        CategoryDto.SaveRequest cat3 = createCategoryDto("temp3", categoryRepository.findById(id2).get());
        Long id3 = categoryService.createCategory(cat3);
        CategoryDto.SaveRequest cat4 = createCategoryDto("temp4", categoryRepository.findById(id2).get());
        Long id4 = categoryService.createCategory(cat4);

        // when
        categoryService.deleteCategory(id1);
        // then
        assertThrows(NotFoundCategoryByIdException.class, () -> categoryRepository.findById(id4).orElseThrow(NotFoundCategoryByIdException::new));
    }
}
