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

    String[] names = {"djkim", "djryu", "cwchoi", "eshong", "yjkim", "asdf", "qwer", "zxcv", "hjkl"}; // 9 명 이하로 선택해줘야합니당

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

    private List<User> createUsers(int n) {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            UserDto.SaveRequest userDto = createUserDto(names[i]);
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
    @DisplayName("null name으로 카테고리 생성 실패 테스트")
    public void createCategoryTestToFailByNullName() throws Exception {
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto(null, root);
        // then
        assertThrows(NotFoundCategoryNameException.class, () -> categoryService.createCategory(cat1));
    }

    @Test
    @DisplayName("empty name으로 카테고리 생성 실패 테스트")
    public void createCategoryTestToFailByEmptyName() throws Exception {
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("", root);

        // then
        assertThrows(NotFoundCategoryNameException.class, () -> categoryService.createCategory(cat1));
    }

    @Test
    @DisplayName("postList == null 로 인한 카테고리 생성 실패 테스트")
    public void createCategoryTestToFailByEmptyPostList() throws Exception {
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
    public void createCategoryTestToFailByEmptyChildCategory() throws Exception {
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
    @DisplayName("자녀의 포스트까지 가져오는지 확인하는 테스트 - 리턴되는 포스트의 수로 판단")
    public void getPostsTestCheckBySize() throws Exception {
        //given
        List<User> users = createUsers(5);
        List<Category> cats = createCategories(100);
        List<Post> posts = createPosts(users, cats, 200);

        // when
        List<Post> result = categoryService.getPosts(cats.get(0).getId());
        // then
        assertThat(200).isEqualTo(posts.size());
        for (Post post : posts) {
            System.out.println(post.getTitle());
        }
    }

    /*
    이 위아래 테스트는 같은 것을 테스트합니다. 아래 스타일은 카테고리 구조를 제가 명시해준 부분 대로 테스트할 수 있는 반면에, 위 스타일은
    카테고리 구조가 랜덤하게 짜여집니다. 카테고리 구조는 오류가 날 수 없는 구조로 짜여지긴하는데, 이렇게 카테고리 구조를 자동으로 생성되게 해서
    테스트를 진행해도 될까요? 아래 테스트처럼 정해진 구조로만 테스트를 돌리면, 혹시 모르는 예외 상황을 알 수 없을 것 같은데, 위와 같은 방식으로 테스트를
    여러번 루프 돌리면 혹시모르는 상황에 대해서도 검증이 가능할 것 같은데, 어떤게 나을지 궁금합니당.

    그리고 가독성 측면에서는 위 코드가 더 나은 것 같은데, 위 테스트를 이해하려면 내부 로직을 봐야되서 어떤게 더 나은지 잘 모르겠습니당
     */

    @Test
    @DisplayName("자녀의 자녀 포스트까지 가져오는지 확인하는 테스트 - 리턴되는 포스트리스트 안에 포스트 수로 확인")
    public void getPostTestMaxDepth() throws Exception {
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
        User user = createUserDto("djkim").toEntity();
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

    // 밑 테스트 코드도 수동으로 카테고리 구조 설정한 것은 주석 처리하였고, 자동화한 부분만 남겨두었습니다.
    @Test
    @DisplayName("포스트를 제대로 가져오는지 확인하는 테스트 - 리턴 되는 포스트리스트 안에 들어있어야할 모든 포스트가 들어있는지 확인")
    public void getPostTestCheckByPosts() throws Exception {
//        Category root = createRoot();
//        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", root);
//        Long id1 = categoryService.createCategory(cat1);
//        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(id1).get());
//        Long id2 = categoryService.createCategory(cat2);
//        CategoryDto.SaveRequest cat3 = createCategoryDto("temp3", categoryRepository.findById(id2).get());
//        Long id3 = categoryService.createCategory(cat3);
//        CategoryDto.SaveRequest cat4 = createCategoryDto("temp4", categoryRepository.findById(id2).get());
//        Long id4 = categoryService.createCategory(cat4);
//        User user = createUserDto("djkim").toEntity();
//        userService.saveUser(user);
//        PostDto.SaveRequest post1 = createPostDto("title1", user, categoryRepository.findById(id1).get());
//        PostDto.SaveRequest post2 = createPostDto("title2", user, categoryRepository.findById(id1).get());
//        PostDto.SaveRequest post3 = createPostDto("title3", user, categoryRepository.findById(id2).get());
//        PostDto.SaveRequest post4 = createPostDto("title4", user, categoryRepository.findById(id3).get());
//        PostDto.SaveRequest post5 = createPostDto("title5", user, categoryRepository.findById(id4).get());
//        PostDto.SaveRequest post6 = createPostDto("title6", user, categoryRepository.findById(id4).get());
//        Long pid1 = postService.SavePost(post1);
//        Long pid2 = postService.SavePost(post2);
//        Long pid3 = postService.SavePost(post3);
//        Long pid4 = postService.SavePost(post4);
//        Long pid5 = postService.SavePost(post5);
//        Long pid6 = postService.SavePost(post6);
        List<User> users = createUsers(5);
        List<Category> cats = createCategories(150);
        List<Post> posts = createPosts(users, cats, 150);

        // when
        List<Post> result = categoryService.getPosts(cats.get(0).getId());
        // then
//        assertThat(result).isEqualTo(posts);
        for (Post post : posts) {
            assertThat(result).contains(post);
        }
        for (Post post : posts) {
            System.out.println(post.getTitle());
        }
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
    @DisplayName("삭제 테스트 - 자녀까지 지워지는지")
    public void deleteTestOneChildOneDepth() throws Exception {
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

    // 여기도 자동화 부분만 남겨두었습니다.
    @Test
    @DisplayName("삭제 테스트 - 다수의 자녀도 지워지는지")
    public void deleteTestMaxDepth() throws Exception {
        //given
//        Category root = createRoot();
//        CategoryDto.SaveRequest cat1 = createCategoryDto("temp", root);
//        Long id1 = categoryService.createCategory(cat1);
//        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(id1).get());
//        Long id2 = categoryService.createCategory(cat2);
//        CategoryDto.SaveRequest cat3 = createCategoryDto("temp3", categoryRepository.findById(id2).get());
//        Long id3 = categoryService.createCategory(cat3);
//        CategoryDto.SaveRequest cat4 = createCategoryDto("temp4", categoryRepository.findById(id2).get());
//        Long id4 = categoryService.createCategory(cat4);
        List<Category> cats = createCategories(7);


        // when
        categoryService.deleteCategory(cats.get(0).getId());
        // then
        for (Category cat : cats) {
            assertThrows(NotFoundCategoryByIdException.class, () -> categoryRepository.findById(cat.getId()).orElseThrow(NotFoundCategoryByIdException::new));
        }
    }
}
