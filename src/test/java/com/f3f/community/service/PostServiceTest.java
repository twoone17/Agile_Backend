package com.f3f.community.service;

import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.service.PostService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class PostServiceTest {
//
//    @Autowired
//    PostService postService;
//
//    @Autowired
//    PostRepository postRepository;

    @Test
    @Rollback
    @DisplayName("업로드")
    public void 업로드(){
        //given
//        PostDto postDto = PostDto.builder()
//                .author(null)
//                .title("title1")
//                .content("content1")
//                .media(null)
//                .viewCount(10)
//                .scrap(null)
//                .comments(null)
//                .likesList(null)
//                .tagList(null)
//                .build();
//
//        Post newPost = postDto.toEntity();
////        //when
//        postRepository.save(newPost);
////        //then
//        assertThat(newPost).isEqualTo(postRepository.findById(newPost.getId()));
//
//    }
    }

}