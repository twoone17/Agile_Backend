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
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    String[] names = {"djkim", "djryu", "cwchoi", "eshong", "yjkim", "asdf", "qwer", "zxcv", "hjkl","rwnw"};

    private UserDto.SaveRequest createUserDto(String name) {
        return new UserDto.SaveRequest(name + "@" + name + ".com", "a1234567@", "01012345678",
                UserGrade.BRONZE, name, "seoul", false);
    }

    private PostDto.SaveRequest createPostDto(String title, User user, Category cat) {
        return PostDto.SaveRequest.builder()
                .title(title)
                .content("temp content")
                .author(user)
                .scrapList(new ArrayList<>())
                .category(cat).build();
    }

    private Category createRoot() throws Exception {
        CategoryDto.SaveRequest cat = createCategoryDto("root", null);
        Long rid = categoryService.createCategory(cat);
        return categoryRepository.findById(rid).get();
    }

    private List<User> createUsers() {
        ArrayList<User> users = new ArrayList<>();
        for (String name : names) {
            UserDto.SaveRequest userDto = createUserDto(name);
            Long uid = userService.saveUser(userDto.toEntity());
            users.add(userRepository.findById(uid).get());
        }
        return users;
    }

    private List<Category> createCategories(int n) throws Exception {
        ArrayList<Category> categories = new ArrayList<>();
        Random random = new Random();
        categories.add(createRoot());
        for (int i = 0; i < n; i++) {
            while (true) {
                try {
                    CategoryDto.SaveRequest categoryDto = createCategoryDto("cat"+i, categories.get(random.nextInt(i+1)));
                    Long cid = categoryService.createCategory(categoryDto);
                    categories.add(categoryRepository.findById(cid).get());
                    break;
                } catch (MaxDepthException e) {
                    continue;
                }
            }
        }
        return categories;
    }


    private List<Post> createPosts(List<User> users, List<Category> cats, int n) throws Exception {
        ArrayList<Post> posts = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            PostDto.SaveRequest postDto = createPostDto("title"+i, users.get(random.nextInt(users.size())), cats.get(random.nextInt(cats.size())));
            Long pid = postService.SavePost(postDto);
            posts.add(postRepository.findById(pid).get());
        }

        return posts;
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
    public void depthInsertionTest() throws Exception {
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
    public void childDepthInsertionTest() throws Exception {
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
    @DisplayName("동일한 이름으로 카테고리 생성 실패 테스트")
    public void createCategoryTestToFailByDuplicateName() throws Exception{
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", root);
        Long id1 = categoryService.createCategory(cat1);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp", root);
        // then
        assertThrows(DuplicateCategoryNameException.class, () -> categoryService.createCategory(cat2));
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
    @DisplayName("자녀 카테고리 리스트 가져오는 테스트")
    public void getChildCategoriesTest() throws Exception{
        //given
        List<Category> categories = createCategories(100);
        for (Category category : categories) {
            // when
            List<Category> childCategories = categoryService.getChildCategories(category.getId());
            List<Category> findByParent = categoryRepository.findCategoriesByParents(category);
            // then
            for (Category cat1 : findByParent) {
                assertThat(childCategories).contains(cat1);
            }
            assertThat(childCategories.size()).isEqualTo(findByParent.size());
        }
    }

    @Test
    @DisplayName("자녀 카테고리에 포스트 리스트 모두 가져오는 테스트")
    public void getChildCategoryPostsTest() throws Exception{
        //given
        List<Category> categories = createCategories(100);
        for (Category category : categories) {
            //when
            List<Post> childPosts = categoryService.getPostsOfChild(category.getId());
            List<Post> findByCategory = postRepository.findPostsByCategory(category);
            //then
            for (Post post : findByCategory) {
                assertThat(childPosts).contains(post);
            }
            assertThat(childPosts.size()).isEqualTo(findByCategory.size());
        }
    }

    @Test
    @DisplayName("루트에 자녀 카테고리 가져오는 테스트")
    public void getChildCategoriesOfRootTest() throws Exception{
        //given
        List<Category> categories = createCategories(100);
        List<Category> childOfRoot = categoryService.getChildsOfRoot();
        List<Category> findByRoot = categoryRepository.findCategoriesByParents(categoryRepository.findByCategoryName("root").get());

        // then
        for (Category category : findByRoot) {
            assertThat(childOfRoot).contains(category);
        }
        assertThat(childOfRoot.size()).isEqualTo(findByRoot.size());
    }

    @Test
    @DisplayName("루트에 자녀 카테고리의 포스트 리스트 가져오는 테스트")
    public void getChildCategoryPostsOfRootTest() throws Exception{
        //given
        List<Category> categories = createCategories(100);
        List<Post> childPostsOfRoot = categoryService.getPostsOfRootChild();
        List<Post> findByRoot = new ArrayList<>();
        for (Category cat : categoryService.getChildsOfRoot()) {
            findByRoot.addAll(cat.getPostList());
        }
        // then
        for (Post post : findByRoot) {
            assertThat(childPostsOfRoot).contains(post);
        }
        assertThat(childPostsOfRoot.size()).isEqualTo(findByRoot.size());

    }

    @Test
    @DisplayName("최고 깊이 도달하여 카테고리 생성안되는 테스트")
    public void createCategoryTestToFailByMaxDepth() throws Exception {
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp1", root);
        Long id1 = categoryService.createCategory(cat1);
        Category category1 = categoryRepository.findById(id1).get();
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", category1);
        Long id2 = categoryService.createCategory(cat2);
        Category category2 = categoryRepository.findById(id2).get();
        CategoryDto.SaveRequest cat3 = createCategoryDto("temp3", category2);
        Long id3 = categoryService.createCategory(cat3);
        Category category3 = categoryRepository.findById(id3).get();

        // when
        CategoryDto.SaveRequest cat4 = createCategoryDto("temp4", category3);


        // then
        assertThrows(MaxDepthException.class, () -> categoryService.createCategory(cat4));
    }

    @Test
    @DisplayName("이름변경 실패 테스트 - 중복된 이름")
    public void updateNameTestToFailByDuplicateName() throws Exception {
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
    @DisplayName("카테고리 삭제 테스트 - 자녀가 없는 경우에만 삭제되는지 확인하는 테스트")
    public void deleteCategoryTest() throws Exception{
        //given
        List<Category> categories = createCategories(100);
        for (Category category : categories) {
            //then
            if (category.getChildCategory().isEmpty()) {
                categoryService.deleteCategory(category.getId());
            } else {
                assertThrows(NotEmptyChildCategoryException.class, () -> categoryService.deleteCategory(category.getId()));
            }

        }

    }



}
