package com.f3f.community.service;


import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.categoryException.DuplicateCategoryNameException;
import com.f3f.community.exception.categoryException.MaxDepthException;
import com.f3f.community.exception.categoryException.NotEmptyChildCategoryException;
import com.f3f.community.exception.categoryException.NotFoundParentCategoryException;
import com.f3f.community.exception.scrapException.DuplicateScrapPostException;
import com.f3f.community.post.domain.Post;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceTestWithDB {
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
        for (int i = 0; i < n; i++) {
            PostDto.SaveRequest postDto = createPostDto("title"+i, userRepository.findById(users.get(random.nextInt(users.size()))).get(), categoryRepository.findById(categories.get(random.nextInt(categories.size()))).get());

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
                    scrapService.saveCollection(sid, uid,posts.get(random.nextInt(posts.size())));
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
        List<Long> users = createUsers(10);
        List<Long> categories = createCategories(20);
        List<Long> posts = createPosts(users, categories, 20);
        List<Long> scraps = createScraps(users, posts, 10);

        // when

        // then

    }


    @Test
    @DisplayName("중복되는 이름으로 카테고리 생성 실패")
    public void createCategoryTestToFailByDuplicateName() throws Exception{
        //given
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);


        // when
        CategoryDto.SaveRequest temp = createCategoryDto("temp", categoryRepository.findById(rid).get());

        // then
        assertThrows(DuplicateCategoryNameException.class, () -> categoryService.createCategory(temp));
    }

    @Test
    @DisplayName("부모가 null 이라서 카테고리 생성 실패")
    public void createCategoryTestToFailByNullParent() throws Exception{
        //given
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", null);
        // then
        assertThrows(NotFoundParentCategoryException.class, () -> categoryService.createCategory(categoryDto));
    }

    @Test
    @DisplayName("최대 깊이로 인한 카테고리 생성 실패")
    public void createCategoryTestToFailByMaxDepth() throws Exception{
        //given
        Long rid = createRoot();
        CategoryDto.SaveRequest cat1 = createCategoryDto("temp1", categoryRepository.findById(rid).get());
        Long cid1 = categoryService.createCategory(cat1);
        CategoryDto.SaveRequest cat2 = createCategoryDto("temp2", categoryRepository.findById(cid1).get());
        Long cid2 = categoryService.createCategory(cat2);
        CategoryDto.SaveRequest cat3 = createCategoryDto("temp3", categoryRepository.findById(cid2).get());
        Long cid3 = categoryService.createCategory(cat3);
        CategoryDto.SaveRequest cat4 = createCategoryDto("temp4", categoryRepository.findById(cid3).get());


        // then
        assertThrows(MaxDepthException.class, () -> categoryService.createCategory(cat4));
    }
    
    @Test
    @DisplayName("카테고리의 자식 카테고리 가져오는 테스트")
    public void getChildCategoryTest() throws Exception{
        //given
        List<Long> categories = createCategories(30);
        for (Long cid : categories) {
            // when
            Category category = categoryRepository.findById(cid).get();
            List<Category> childCategory = categoryRepository.findCategoriesByParents(category);
            List<Long> childIds = new ArrayList<>();
            for (Category child : childCategory) {
                childIds.add(child.getId());
            }
            List<Category> childCategories = categoryService.getChildCategories(cid);
            // then
            for (Category child : childCategories) {
                assertThat(childIds).contains(child.getId());
            }
            assertThat(childIds.size()).isEqualTo(childCategories.size());
        }

    }

    @Test
    @DisplayName("카테고리의 자식 카테고리 포스트 가져오는 테스트")
    public void getChildsPostTest() throws Exception{
        //given
        List<Long> users = createUsers(10);
        List<Long> categories = createCategories(20);
        List<Long> posts = createPosts(users, categories, 20);
        for (Long cid : categories) {
            List<Post> postsOfChild = categoryService.getPostsOfChild(cid);
            List<Post> postsByCategory = new ArrayList<>();
            for (Category child : categoryService.getChildCategories(cid)) {
                postsByCategory.addAll(postRepository.findPostsByCategory(child));
            }
            List<Long> postsOfChildId = new ArrayList<>();
            List<Long> postsByCategoryId = new ArrayList<>();
            for (Post post : postsOfChild) {
                postsOfChildId.add(post.getId());
            }
            for (Post post : postsByCategory) {
                postsByCategoryId.add(post.getId());
            }
            Collections.sort(postsOfChildId);
            Collections.sort(postsByCategoryId);
            assertThat(postsOfChildId).isEqualTo(postsByCategoryId);
        }
        // when

        // then
    }

    @Test
    @DisplayName("카테고리 삭제 테스트 - 자녀가 없는 경우에만 삭제되는지 확인하는 테스트")
    public void deleteCategoryTest() throws Exception{
        //given
        List<Long> categories = createCategories(100);
        for (Long cid : categories) {
            //then
            Category category = categoryRepository.findById(cid).get();
            List<Category> child = categoryService.getChildCategories(cid);
            if (child.isEmpty()) {
                categoryService.deleteCategory(category.getId());
            } else {
                assertThrows(NotEmptyChildCategoryException.class, () -> categoryService.deleteCategory(category.getId()));
            }

        }

    }
}
