package com.f3f.community.service;

import com.f3f.community.exception.postException.NotFoundPostAuthorException;
import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.postException.NotFoundPostContentException;
import com.f3f.community.exception.postException.NotFoundPostTitleException;
import com.f3f.community.exception.scrapException.NotFoundScrapUserException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.dto.PostDto.SaveRequest;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import com.sun.xml.bind.v2.TODO;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static com.f3f.community.post.dto.PostDto.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class PostServiceTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    //Create Test
    //TODO: test를 저장한 postid를 저장소에서 찾는것으로만 검증하면 될까? 더 좋은방식은 없을깡
    @Test
    @Rollback
    @DisplayName("Service : savePost 성공 테스트")
    //필수값이 다 들어감 : 통과
    public void savePostTestToOk() throws Exception {
        //given
        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
                .author(new User())
                .title("title1")
                .content("content1")
                .build();

        //when
        Long postid = postService.SavePost(SaveRequest); //SavePost한 후 postid를 반환
        //then :  postRepository에 postid인 post가 저장되어있는지 확인, 없으면 exception
        postRepository.findById(postid).orElseThrow(NotFoundPostByIdException::new);

    }

    @Test
    @Rollback
    @DisplayName("Service : savePost 예외 발생 테스트 - author 없음  ")
    //필수값 author 없음 : 실패
    public void savePostTestToFailByNullAuthor() throws Exception {
        //given
        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
//                .author(new User())
                .title("title1")
                .content("content1")
                .build();

        //then :  postService의 SavePost할때 일어나는 exception이 앞 인자의 exception class와 같은지 확인
        assertThrows(NotFoundPostAuthorException.class, ()-> postService.SavePost(SaveRequest));

    }

    @Test
    @Rollback
    @DisplayName("Service : savePost 예외 발생 테스트 - title 없음  ")
    //필수값 title 없음 : 실패
    public void savePostTestToFailByNullTitle() throws Exception {
        //given
        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
                .author(new User())
//                .title("title1")
                .content("content1")
                .build();

        //then :  postService의 SavePost할때 일어나는 exception이 앞 인자의 exception class와 같은지 확인
        assertThrows(NotFoundPostTitleException.class, ()-> postService.SavePost(SaveRequest));

    }

    @Test
    @Rollback
    @DisplayName("Service : savePost 예외 발생 테스트 - content 없음  ")
    //필수값 title 없음 : 실패
    public void savePostTestToFailByNullContent() throws Exception {
        //given
        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
                .author(new User())
                .title("title1")
//                .content("content1")
                .build();

        //then :  postService의 SavePost할때 일어나는 exception이 앞 인자의 exception class와 같은지 확인
        assertThrows(NotFoundPostContentException.class, ()-> postService.SavePost(SaveRequest));

    }

    //

}