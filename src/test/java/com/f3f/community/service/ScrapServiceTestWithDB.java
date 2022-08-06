package com.f3f.community.service;


import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
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
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScrapServiceTestWithDB {
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


    @Test
    @DisplayName("디비에 데이터 들어가는 테스트")
    public void dbInsertionTest() throws Exception{
        //given
        UserDto.SaveRequest temp = createUserDto("temp");
        Long uid = userService.saveUser(temp);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest postDto = createPostDto("tempPost", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);
        ScrapDto.SaveRequest scrapDto = createScrapDto(userRepository.findById(uid).get(), "tempScrap");
        Long sid = scrapService.createScrap(scrapDto);

        // when
        scrapService.saveCollection(sid, pid);
        List<Post> result = scrapPostService.getPostsOfScrap(sid);
        // then
//        assertThat(result).contains(postRepository.findById(pid).get());
        assertThat(result.get(0).getTitle()).isEqualTo(postRepository.findById(pid).get().getTitle());

        scrapPostRepository.deleteAll();
        scrapRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

    }
}
