package com.f3f.community.service;

import com.f3f.community.exception.postException.*;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.dto.PostDto.SaveRequest;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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

    private UserDto.SaveRequest createUserDto1(){
        return new UserDto.SaveRequest("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
    }

    //Dto create TODO: 주석 처리 한것은 왜 빌드가 안되는지 ?
    private PostDto.SaveRequest createPostDto1(User user) {
        return SaveRequest.builder()
                .author(user)
                .title("title1")
                .content("content1")
//                .media((List<Media>) new Media())
                .viewCount(1000)
                .scrap(new Scrap())
//                .comments((List<Comment>) new Comment())
//                .likesList((List<Likes>) new Likes())
//                .tagList((List<PostTag>) new PostTag())
                .build();
    }

//    @AfterEach
//    void clear() {
//        userRepository.deleteAll();
//        postRepository.deleteAll();
//    }


    /**
     * 게시글 작성 테스트 (Create Test)
     */
    //TODO: test를 저장한 postid를 저장소에서 찾는것으로만 검증하면 될까? 더 좋은방식은 없을깡
    @Test
    @Rollback
    @DisplayName("Service : savePost 성공 테스트")
    //필수값이 다 들어감 : 통과
    public void savePostTestToOk() throws Exception {
        //given
//        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
//                .author(new User())
//                .title("title1")
//                .content("content1")
//                .build();
        UserDto.SaveRequest userDto1 = createUserDto1();
        User user = userDto1.toEntity();
        PostDto.SaveRequest postDto1 = createPostDto1(user);

        //when
        Long postid = postService.SavePost(postDto1); //SavePost한 후 postid를 반환
        //then :  postRepository에 postid인 post가 저장되어있는지 확인, 없으면 exception
        postRepository.findById(postid).orElseThrow(NotFoundPostByIdException::new);
//        System.out.println("postRepository.findByUser(user)" + postRepository.findByUser(user));

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

    /**
     * 게시글 조회 테스트( Read Test)
     */

    @Test
    @Rollback()
    @DisplayName("Service : findPostByPostId 성공 테스트")
    public void findPostByPostIdTestOk() throws Exception{
        //given
        //TODO : 이렇게 진행을 하면 post와 연관되어있는 user의 FK를 찾을수 없어 문제가 발생한다, 이때 CASCADE TYPE 을 ALL로 바꿔주면 되는것같은데, 이렇게 해도 되는건지
//        UserDto.SaveRequest userDto1 = createUserDto1();
//        User user = userDto1.toEntity();
//        SaveRequest postDto1 = createPostDto1(user);
//        Post post = postDto1.toEntity();


        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
                .author(new User())
                .title("title1")
                .content("content1")
                .build();
        Post post = SaveRequest.toEntity();


        //when
        postRepository.save(post);

        //then
        assertThat(post).isEqualTo(postService.findPostByPostId(post.getId()).get()); //postid로 조회한 post가 일치하는지 확인
        assertThrows(NoPostByPostIdException.class, ()-> postService.findPostByPostId(44L));  //존재하지 않는 postid로 조회했을떄 exception이 터지는지 확인

    }

}