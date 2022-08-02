package com.f3f.community.repository;

import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    @Rollback
    @DisplayName(value = "업로드")
    public void savePost(){
        //given
        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
                .title("title1")
                .content("content1")
                .build();

        Post newPost = SaveRequest.toEntity();
//        //when
        postRepository.save(newPost);
//        //then
        assertThat(newPost).isEqualTo(postRepository.findById(newPost.getId()).get());
        //optional은 .get()으로 받는다
    }
//    @Test
//    @DisplayName(value = "title로 찾기")
//    public void readByTitle() throws Exception{
//        //given
//        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
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
//        Post newPost = SaveRequest.toEntity();
//        //when
//        postRepository.save(newPost);
//        //then
//        Post title1 = postRepository.findByTitle("title1");
//        assertThat(newPost).isEqualTo(title1);
//
//    }

}