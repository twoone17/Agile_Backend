package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.categoryException.MaxDepthException;
import com.f3f.community.exception.scrapException.DuplicateScrapPostException;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.repository.ScrapPostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostServiceTestWithDB {
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
    ScrapPostRepository scrapPostRepository;

    private List<Long> createUsers(int n){
        List<Long> uids = new ArrayList<>();
        for(int i = 0; i<n; i++){
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
        for(int i =0 ; i < n ; i++){
            PostDto.SaveRequest postDto = createPostDto("title" + i, userRepository.findById(users.get(random.nextInt(users.size()))).get(), categoryRepository.findById(categories.get(random.nextInt(categories.size()))).get());
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
                    scrapService.saveCollection(sid,uid,posts.get(random.nextInt(posts.size())));
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
}
