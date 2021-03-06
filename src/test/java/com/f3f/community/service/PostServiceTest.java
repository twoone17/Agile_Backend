package com.f3f.community.service;

import com.f3f.community.exception.postException.*;
import com.f3f.community.media.domain.Media;
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

import java.util.List;

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

    private UserDto.SaveRequest createUserDto2(){
        return new UserDto.SaveRequest("temp2@temp.com", "123456", "010123456782",
                UserGrade.BRONZE, "euisung", "seoul");
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
    @Rollback()
    @DisplayName("Service : savePost 성공 테스트")
    //필수값이 다 들어감 : 통과
    public void savePostTestToOk() throws Exception {
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User user = userDto1.toEntity();
        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(user)
                .title("title1")
                .content("content1")
                .build();
//        PostDto.SaveRequest postDto1 = createPostDto1(user);

        //when
        Long postid = postService.SavePost(postDto1); //SavePost한 후 postid를 반환
        //then :  postRepository에 postid인 post가 저장되어있는지 확인, 없으면 exception
        postRepository.findById(postid).orElseThrow(NotFoundPostByIdException::new);
//        System.out.println("postRepository.findByAuthor(user)" + postRepository.findByAuthor(user));

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
    //Read a-1) post_id로 post 찾기
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

    }

    @Test
    @Rollback()
    @DisplayName("Service : findPostByPostId 예외 발생 테스트 - postid 존재하지 않음 ")
    public void findPostByPostIdTestToFailByNullPostId() throws Exception{

        assertThrows(NotFoundPostByPostIdException.class, ()-> postService.findPostByPostId(44L));  //존재하지 않는 postid로 조회했을떄 exception이 터지는지 확인

    }

    @Test
    @DisplayName("Service : findPostListByAuthor 성공 테스트 (post 하나 저장)")
    public void findPostListByAuthorTest_One_Ok() throws Exception{
    //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();
        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title1")
                .content("content1")
                .build();
    //when
        Long postid = postService.SavePost(postDto1); //SavePost한 후 postid를 반환

    //then
        List<Post> postListByAuthor = postService.findPostListByAuthor(author); //author에 해당하는 postList 찾기
        assertThat(postListByAuthor).contains(postRepository.findById(postid).get()); //postList에 저장한 post가 담겨있는지 확인

    }

    @Test
    @Rollback()
    @DisplayName("Service : findPostListByAuthor 성공 테스트 (post 여러개 저장)")
    public void findPostListByAuthorTest_Multiple_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        UserDto.SaveRequest userDto2 = createUserDto2();

        User author1 = userDto1.toEntity();
        User author2 = userDto2.toEntity();

        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author1)
                .title("title1")
                .content("content1")
                .build();

        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
                .author(author1)
                .title("title2")
                .content("content2")
                .build();

        PostDto.SaveRequest postDto3 = PostDto.SaveRequest.builder()
                .author(author1)
                .title("title3")
                .content("content3")
                .build();

        //다른 유저가 저장
        PostDto.SaveRequest postDto4 = PostDto.SaveRequest.builder()
                .author(author2)
                .title("title4")
                .content("content4")
                .build();
        //when
        Long postid1 = postService.SavePost(postDto1); //author1 게시글 저장
        Long postid2 = postService.SavePost(postDto2); //author1 게시글 저장
        Long postid3 = postService.SavePost(postDto3); //author1 게시글 저장
        Long postid4 = postService.SavePost(postDto4); //author2 게시글 저장 ( 위 3개와 다른 유저)


        //then
        List<Post> postListByAuthor = postService.findPostListByAuthor(author1); //author1에 해당하는 postList 찾기
        assertThat(postListByAuthor).contains(postRepository.findById(postid1).get(),
                                              postRepository.findById(postid2).get(),
                                              postRepository.findById(postid3).get())
                                    .doesNotContain(postRepository.findById(postid4).get()); //author2 는 포함되어있지 않아야함

        assertThat(3).isEqualTo(postListByAuthor.size()); //author1에 해당하는 postList가 3개인지 확인

    }

    @Test
    @DisplayName("Service : findPostListByAuthor 예외 발생 테스트 - author에 해당하는 postList 없음")
    public void findPostListByAuthorTestToFailByNullPostList() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();

        UserDto.SaveRequest userDto2 = createUserDto2();
        User author2 = userDto2.toEntity();
        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title1")
                .content("content1")
                .build();
        //when
        Long postid = postService.SavePost(postDto1); //SavePost한 후 postid를 반환

        //then
        assertThrows(NotFoundPostListByAuthor.class, ()-> postService.findPostListByAuthor(author2));
        //TODO: author2를 넣었으니 예외가 발생하는 그림을 원했는데
        // <org.springframework.dao.InvalidDataAccessApiUsageException>
        // save the transient instance before flushing: com.f3f.community.user.domain.User
        // 이렇게 뜬다, CASCADE ALL을 해서 고쳐야 하나요? 어떻게 해결할지 모르겠습니당 ..

    }


    @Test
    @DisplayName("Service : findPostListByTitle 성공 테스트 (일치하는 title 하나) ")
    public void findPostListByTitleTest_One_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();
        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title1")
                .content("content1")
                .build();
        //when
        Long postid = postService.SavePost(postDto1); //SavePost한 후 postid를 반환

        //then
        List<Post> postListBytitle = postService.findPostListByTitle("title1");//title에 해당하는 postList 찾기
        assertThat(postListBytitle).contains(postRepository.findById(postid).get()); //postList에 저장한 post가 담겨있는지 확인

    }


    @Test
    @DisplayName("Service : findPostListByTitle 성공 테스트 (일치하는 title 여러개) ")
    public void findPostListByTitleTest_Multiple_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        UserDto.SaveRequest userDto2 = createUserDto2();

        User author1 = userDto1.toEntity();
        User author2 = userDto2.toEntity();

        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author1)
                .title("title1")
                .content("content1")
                .build();

        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
                .author(author1)
                .title("title2")
                .content("content2")
                .build();

        PostDto.SaveRequest postDto3 = PostDto.SaveRequest.builder()
                .author(author1)
                .title("title3")
                .content("content3")
                .build();

        //다른 유저가 저장
        PostDto.SaveRequest postDto4 = PostDto.SaveRequest.builder()
                .author(author2)
                .title("title1")
                .content("content4")
                .build();
        //when
        Long postid1 = postService.SavePost(postDto1); //title1 게시글 저장
        Long postid2 = postService.SavePost(postDto2); //title2 게시글 저장
        Long postid3 = postService.SavePost(postDto3); //title3 게시글 저장
        Long postid4 = postService.SavePost(postDto4); //title1 게시글 저장 ( 위 3개와 다른 유저)

        List<Post> postListBytitle = postService.findPostListByTitle("title1");//author1에 해당하는 postList 찾기
        assertThat(postListBytitle).contains(postRepository.findById(postid1).get(), //title1 포함
                                             postRepository.findById(postid4).get())
                                   .doesNotContain(postRepository.findById(postid2).get(),
                                                   postRepository.findById(postid3).get()); //title 2, 3 포함 x

        assertThat(2).isEqualTo(postListBytitle.size()); //title1 해당하는 postList가 2개인지 확인
    }

    @Test
    @DisplayName("Service : findPostListByTitle 예외 발생 테스트 - 일치하는 title 없음")
    public void findPostListByTitleTestToFailByNullTitle() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();
        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title2")
                .content("content1")
                .build();
        //when
        Long postid = postService.SavePost(postDto1); //SavePost한 후 postid를 반환

        //then
        List<Post> postListBytitle = postService.findPostListByTitle("title2");//title에 해당하는 postList 찾기
        System.out.println("postListBytitle = " + postListBytitle);
        assertThrows(NotFoundPostListByTitle.class, ()-> postService.findPostListByTitle("title1")); //title1에 해당하는 title은 없으므로 exception 터트림

    }

    @Test
    @Rollback(false)
    @DisplayName("Update")
    public void updateTitle() throws Exception{
    //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();
        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title2")
                .content("content1")
                .build();

        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title3")
                .content("content1")
                .build();

        PostDto.UpdateRequest updateRequest = PostDto.UpdateRequest.builder()
                .title("titleChanged")
                .content("contentChanged")
//                .media(Media)
                .build();
        //when

        Long postid = postService.SavePost(postDto1); //SavePost한 후 postid를 반환
        Long postid2 = postService.SavePost(postDto2); //SavePost한 후 postid를 반환
        List<Post> postListByAuthor = postService.findPostListByAuthor(author);
        System.out.println("postListByAuthor = " + postListByAuthor);
        //then
        postService.UpdatePost(postid,updateRequest);
        System.out.println("postListByAuthor = " + postListByAuthor);
        System.out.println("postListByAuthor = " + postListByAuthor.get(1));
    }

}