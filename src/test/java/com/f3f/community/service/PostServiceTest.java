package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.postException.*;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.ScrapPost;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.dto.PostDto.SaveRequest;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
//import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@TestMethodOrder(value = MethodOrderer.DisplayName.class)
class PostServiceTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    private UserDto.SaveRequest createUserDto1(){
        return new UserDto.SaveRequest("temp@temp.com", "123456", "01012345678",
                UserGrade.BRONZE, "james", "changwon");
    }

    private UserDto.SaveRequest createUserDto2(){
        return new UserDto.SaveRequest("temp2@temp.com", "123456", "010123456782",
                UserGrade.BRONZE, "euisung", "seoul");
    }

    //Dto create
    private PostDto.SaveRequest createPostDto1(User user, Category cat) {
        return SaveRequest.builder()
                .author(user)
                .title("title1")
                .content("content1")
//                .media((List<Media>) new Media())
                .viewCount(1000)
                .scrapList(new ArrayList<ScrapPost>())
//                .comments((List<Comment>) new Comment())
//                .likesList((List<Likes>) new Likes())
//                .tagList((List<PostTag>) new PostTag())
                .category(cat)
                .build();
    }

    private PostDto.SaveRequest createPostDto2(User user, Category cat) {
        return SaveRequest.builder()
                .author(user)
                .title("title2")
                .content("content2")
//                .media((List<Media>) new Media())
                .viewCount(1000)
                .scrapList(new ArrayList<ScrapPost>())
//                .comments((List<Comment>) new Comment())
//                .likesList((List<Likes>) new Likes())
//                .tagList((List<PostTag>) new PostTag())
                .category(cat)
                .build();
    }

    private CategoryDto.SaveRequest createCategoryDto(String name, Category parent) {
        return CategoryDto.SaveRequest.builder()
                .categoryName(name)
                .childCategory(new ArrayList<>())
                .parents(parent)
                .postList(new ArrayList<>()).build();
    }

    private Category createRoot() throws Exception{
        CategoryDto.SaveRequest cat = createCategoryDto("root", null);
        Long rid = categoryService.createCategory(cat);
        return categoryRepository.findById(rid).get();
    }

//    @AfterEach
//    void clear() {
//        userRepository.deleteAll();
//        postRepository.deleteAll();
//    }

    /*************************************************************************************
     * 게시글 작성 테스트 (Create)
     **************************************************************************************/
    @Test
    @Rollback()
    @DisplayName("1 Save-1: savePost 성공 테스트")
    //필수값이 다 들어감 : 통과
    public void savePostTestToOk() throws Exception {
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User user = userDto1.toEntity();
        userRepository.save(user);

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        PostDto.SaveRequest postDto1 = createPostDto1(user,cat);

        //when
        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환
        //then :  postRepository에 postid인 post가 저장되어있는지 확인, 없으면 exception
        postRepository.findById(postid).orElseThrow(NotFoundPostByIdException::new);
//        System.out.println("postRepository.findByAuthor(user)" + postRepository.findByAuthor(user));

    }



    @Test
    @Rollback
    @DisplayName("1 Save-2: savePost 예외 발생 테스트 - author 없음  ")
    //필수값 author 없음 : 실패
    public void savePostTestToFailByNullAuthor() throws Exception {
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User user = userDto1.toEntity();
        userRepository.save(user);

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        //when : author 없음
        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
//                .author(new User())
                .title("title1")
                .content("content1")
                .category(cat)
                .build();


        //then :  postService의 SavePost할때 일어나는 exception이 앞 인자의 exception class와 같은지 확인
        assertThrows(ConstraintViolationException.class, ()->   postService.savePost(SaveRequest));

    }

    @Test
    @Rollback()
    @DisplayName("1 Save-3: savePost 예외 발생 테스트 - title 없음  ")
    //필수값 title 없음 : 실패
    public void savePostTestToFailByNullTitle() throws Exception {
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
                .author(new User())
//                .title("title1")
                .content("content1")
                .category(cat)
                .build();

        //then :  postService의 SavePost할때 일어나는 exception이 앞 인자의 exception class와 같은지 확인 TODO: Global Exception
//        assertThrows(NotFoundPostTitleException.class, ()-> postService.savePost(SaveRequest));
        assertThrows(ConstraintViolationException.class, ()->   postService.savePost(SaveRequest));

    }

    @Test
    @Rollback
    @DisplayName("1 Save-4: savePost 예외 발생 테스트 - content 없음  ")
    //필수값 content 없음 : 실패
    public void savePostTestToFailByNullContent() throws Exception {
        //given
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();
        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
                .author(new User())
                .title("title1")
//                .content("content1")
                .category(cat)
                .build();

        //then :  postService의 SavePost할때 일어나는 exception이 앞 인자의 exception class와 같은지 확인 TODO: Global Exception
//      assertThrows(NotFoundPostContentException.class, ()-> postService.savePost(SaveRequest));
        assertThrows(ConstraintViolationException.class, ()->   postService.savePost(SaveRequest));
    }

    /*************************************************************************************
     * 게시글 조회 (Read)
     **************************************************************************************/
    //Read a-1) post_id로 post 찾기
    @Test
    @Rollback()
    @DisplayName("2 Read-1 : findPostByPostId 성공 테스트")
    public void findPostByPostIdTestOk() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User user = userDto1.toEntity();
//        SaveRequest postDto1 = createPostDto1(user);
//        Post post = postDto1.toEntity();
        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        PostDto.SaveRequest SaveRequest = PostDto.SaveRequest.builder()
                .author(user)
                .title("title1")
                .content("content1")
                .category(cat)
                .build();
        Post post = SaveRequest.toEntity();


        //when
        postRepository.save(post);
        userRepository.save(user);

        //then
        assertThat(post).isEqualTo(postService.findPostByPostId(post.getId()).get()); //postid로 조회한 post가 일치하는지 확인

    }

    @Test
    @Rollback()
    @DisplayName("2 Read-2 : findPostByPostId 예외 발생 테스트 - postid 존재하지 않음 ")
    public void findPostByPostIdTestToFailByNullPostId() throws Exception{
    //TODO: 저장하고 지운것을 테스트 해보기
        assertThrows(NotFoundPostByPostIdException.class, ()-> postService.findPostByPostId(44L));  //존재하지 않는 postid로 조회했을떄 exception이 터지는지 확인

    }

    @Test
    @DisplayName("2 Read-3 : findPostListByUserid 성공 테스트")
    public void findPostListByUseridTest_One_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User user = userDto1.toEntity();
        userRepository.save(user);
        Long userId1 = user.getId();

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        SaveRequest postDto1 = createPostDto1(user,cat);
        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환

        //when
        List<Post> postListByUserId = postService.findPostListByUserId(userId1);

        //then
        assertThat(postListByUserId).contains(postRepository.findById(postid).get()); //postList에 저장한 post가 담겨있는지 확인


    }

    @Test
    @DisplayName("2 Read-4 : findPostListByUserid 성공 테스트 : 여러개 ")
    public void findPostListByUseridTest_Multiple_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User user = userDto1.toEntity();
        userRepository.save(user);

        UserDto.SaveRequest userDto2 = createUserDto2();
        User user2 = userDto2.toEntity();
        userRepository.save(user2);

        Long userId1 = user.getId();
        Long userId2 = user2.getId();

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        SaveRequest postDto1 = createPostDto1(user,cat);
        SaveRequest postDto2 = createPostDto2(user,cat);
        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환
        Long postid2 = postService.savePost(postDto1); //SavePost한 후 postid를 반환

        //when
        List<Post> postListByUserId = postService.findPostListByUserId(userId1);

        //then
        assertThat(postListByUserId).contains(postRepository.findById(postid).get(),
                postRepository.findById(postid2).get()); //postList에 저장한 post가 담겨있는지 확인

        assertThat(2).isEqualTo(postListByUserId.size()); //author1에 해당하는 postList가 3개인지 확인

    }

    @Test
    @DisplayName("2 Read-4 : findPostListByUserid 예외 발생 테스트 - userid와 일치하는 게시글 없음")
    public void findPostListByUseridTestToFailByNullPostList() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User user = userDto1.toEntity();
        userRepository.save(user);
        Long userId1 = user.getId();

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        SaveRequest postDto1 = createPostDto1(user,cat);
        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환

        //when
        postService.deletePost(postid,userId1);

        //then
        assertThrows(NotFoundPostByUserIdException.class, ()-> postService.findPostListByUserId(userId1));

    }



    @Test
    @DisplayName("2 Read-6 : findPostListByTitle 성공 테스트 (일치하는 title 하나) ")
    public void findPostListByTitleTest_One_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title1")
                .content("content1")
                .category(cat)
                .build();

        //when
        Long uid = userService.saveUser(userDto1);
        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환
        userRepository.save(author);

        //then
        List<Post> postListBytitle = postService.findPostListByTitle("title1");//title에 해당하는 postList 찾기
        assertThat(postListBytitle).contains(postRepository.findById(postid).get()); //postList에 저장한 post가 담겨있는지 확인

    }


    @Test
    @DisplayName("2 Read-7 : findPostListByTitle 성공 테스트 (일치하는 title 여러개) ")
    public void findPostListByTitleTest_Multiple_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        UserDto.SaveRequest userDto2 = createUserDto2();

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        User author1 = userDto1.toEntity();
        User author2 = userDto2.toEntity();

        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author1)
                .title("title1")
                .content("content1")
                .category(cat)
                .build();

        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
                .author(author1)
                .title("title2")
                .content("content2")
                .category(cat)
                .build();

        PostDto.SaveRequest postDto3 = PostDto.SaveRequest.builder()
                .author(author1)
                .title("title3")
                .content("content3")
                .category(cat)
                .build();

        //다른 유저가 저장
        PostDto.SaveRequest postDto4 = PostDto.SaveRequest.builder()
                .author(author2)
                .title("title1")
                .content("content4")
                .category(cat)
                .build();
        //when
        Long uid1 = userService.saveUser(userDto1);
        Long uid2 = userService.saveUser(userDto2);
        Long postid1 = postService.savePost(postDto1); //title1 게시글 저장
        Long postid2 = postService.savePost(postDto2); //title2 게시글 저장
        Long postid3 = postService.savePost(postDto3); //title3 게시글 저장
        Long postid4 = postService.savePost(postDto4); //title1 게시글 저장 ( 위 3개와 다른 유저)

        userRepository.save(author1);
        userRepository.save(author2);

        List<Post> postListBytitle = postService.findPostListByTitle("title1");//author1에 해당하는 postList 찾기
        assertThat(postListBytitle).contains(postRepository.findById(postid1).get(), //title1 포함
                                             postRepository.findById(postid4).get())
                                   .doesNotContain(postRepository.findById(postid2).get(),
                                                   postRepository.findById(postid3).get()); //title 2, 3 포함 x

        assertThat(2).isEqualTo(postListBytitle.size()); //title1 해당하는 postList가 2개인지 확인
    }

    @Test
    @DisplayName("2 Read-8 : findPostListByTitle 예외 발생 테스트 - 일치하는 title 없음")
    public void findPostListByTitleTestToFailByNullTitle() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title2")
                .content("content1")
                .category(cat)
                .build();
        //when
        Long uid = userService.saveUser(userDto1);
        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환

        //then
        userRepository.save(author);
        List<Post> postListBytitle = postService.findPostListByTitle("title2");//title에 해당하는 postList 찾기
        System.out.println("postListBytitle = " + postListBytitle);
        assertThrows(NotFoundPostListByTitle.class, ()-> postService.findPostListByTitle("title1")); //title1에 해당하는 title은 없으므로 exception 터트림

    }

    /*************************************************************************************
    * 게시글 수정 (Update)
    **************************************************************************************/

    @Test
    @Rollback()
    @DisplayName("3 Update-1 : UpdatePost 성공 테스트")
    public void updatePostTestToOk() throws Exception{
    //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();
        userRepository.save(author);
        Long userId1 = author.getId();

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title2")
                .content("content1")
                .category(cat)
                .build();

        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title3")
                .content("content1")
                .category(cat)
                .build();

        PostDto.UpdateRequest updateRequest = PostDto.UpdateRequest.builder()
                .title("titleChanged")
                .content("contentChanged")
//                .media(Media)
                .build();
        //when

        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환
        Long postid2 = postService.savePost(postDto2); //SavePost한 후 postid를 반환
        List<Post> postListByAuthor = postService.findPostListByUserId(userId1);
        //then
        postService.updatePost(postid,userId1,updateRequest);
//        for (Post post : postListByAuthor) {
//            System.out.println(postListByAuthor);
//            System.out.println("post = " + post.getTitle()); //postListByAuthor 에서도 title이 바뀌었는지 확인
//            System.out.println("post = " + post.getAuthor());
//        }

        assertThat(postRepository.findById(postid).get().getTitle()).isEqualTo("titleChanged"); //title이 잘 update되었는지 확인
        assertThat(postRepository.findById(postid).get().getContent()).isEqualTo("contentChanged"); //title이 잘 변경되었는지 확인
        assertThat(postListByAuthor.get(0).getTitle()).isEqualTo("titleChanged"); //postListByAuthor 에서도 title이 바뀌었는지 확인
    }

    @Test
    @Rollback()
    @DisplayName("3 Update-2 : UpdatePost 예외 발생 테스트 - 수정하려는 post의 postid가 없음")
    public void updatePostTestToFailByNullPostId() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();
        Long user1Id = userService.saveUser(userDto1);

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title2")
                .content("content1")
                .category(cat)
                .build();

        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title3")
                .content("content1")
                .category(cat)
                .build();

        PostDto.UpdateRequest updateRequest = PostDto.UpdateRequest.builder()
                .title("titleChanged")
                .content("contentChanged")
//                .media(Media)
                .build();
        //when

        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환
        Long postid2 = postService.savePost(postDto2); //SavePost한 후 postid를 반환

        //then
        //존재하지 않는 postid를 수정하려고 했을떄 예외처리
        assertThrows(NotFoundPostByIdException.class, ()-> postService.updatePost(44L,user1Id,updateRequest));
    }

    //필요 없음
//    @Test
//    @Rollback()
//    @DisplayName("3 Update-3 : UpdatePost 예외 발생 테스트 - 수정하려는 post가 본인의 게시글이 아님")
//    public void updatePostTestToFailByNullPostInAuthor() throws Exception{
//        //given
//        UserDto.SaveRequest userDto1 = createUserDto1();
//        User author = userDto1.toEntity();
//        Long user1Id = userService.saveUser(userDto1);
//
//        Category root = createRoot();
//        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
//        Long cid = categoryService.createCategory(categoryDto);
//        Category cat = categoryRepository.findById(cid).get();
//
//        UserDto.SaveRequest userDto2 = createUserDto2();
//        User author2 = userDto2.toEntity();
//        Long user2Id = userService.saveUser(userDto2);
//        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
//                .author(author)
//                .title("title2")
//                .content("content1")
//                .category(cat)
//                .build();
//
//        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
//                .author(author)
//                .title("title3")
//                .content("content1")
//                .category(cat)
//                .build();
//
//        PostDto.UpdateRequest updateRequest = PostDto.UpdateRequest.builder()
//                .title("titleChanged")
//                .content("contentChanged")
////                .media(Media)
//                .build();
//        //when
//
//        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환
//        Long postid2 = postService.savePost(postDto2); //SavePost한 후 postid를 반환
//
//        //then
//        //수정하려는 post가 자신의 게시글이 아닐때 예외처리 TODO: Global Exception
//        assertThrows(NotFoundPostInAuthorException.class, ()-> postService.updatePost(postid,user2Id,updateRequest));
//    }

    @Test
    @Rollback()
    @DisplayName("3 Update-4 : UpdatePost 예외 발생 테스트 - 수정시에 Title이 한글자 미만")
    public void updatePostTestToFailByNullTitle() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();
        Long user1Id = userService.saveUser(userDto1);
        userRepository.save(author);

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        UserDto.SaveRequest userDto2 = createUserDto2();
        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title2")
                .content("content1")
                .category(cat)
                .build();

        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title3")
                .content("content1")
                .category(cat)
                .build();

        PostDto.UpdateRequest updateRequest = PostDto.UpdateRequest.builder()
                .title("")
                .content("contentChanged")
//                .media(Media)
                .build();

        PostDto.UpdateRequest updateRequest2 = PostDto.UpdateRequest.builder()
                .title("")
                .content("contentChanged")
//                .media(Media)
                .build();
        //when

        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환
        Long postid2 = postService.savePost(postDto2); //SavePost한 후 postid를 반환

        //then
        //수정시 title이 안들어감 TODO: Global Exeption
//        assertThrows(NotFoundPostTitleException.class, ()-> postService.updatePost(postid,user1Id,updateRequest));
//        assertThrows(NotFoundPostTitleException.class, ()-> postService.updatePost(postid,user1Id,updateRequest2));
        assertThrows(ConstraintViolationException.class, ()-> postService.updatePost(postid,user1Id,updateRequest));
        assertThrows(ConstraintViolationException.class, ()-> postService.updatePost(postid,user1Id,updateRequest2));




    }

    @Test
    @Rollback()
    @DisplayName("3 Update-5 : UpdatePost 예외 발생 테스트 - 수정시에 content가 한글자 미만")
    public void updatePostTestToFailByNullContent() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();
        Long user1Id = userService.saveUser(userDto1);

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        UserDto.SaveRequest userDto2 = createUserDto2();
        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title2")
                .content("content1")
                .category(cat)
                .build();

        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title3")
                .content("content1")
                .category(cat)
                .build();

        PostDto.UpdateRequest updateRequest = PostDto.UpdateRequest.builder()
                .title("titlechanged")
                .content("")
//                .media(Media)
                .build();
        //when

        PostDto.UpdateRequest updateRequest2 = PostDto.UpdateRequest.builder()
                .title("titlechanged")
//                .content("")
//                .media(Media)
                .build();

        Long postid = postService.savePost(postDto1); //SavePost한 후 postid를 반환
        Long postid2 = postService.savePost(postDto2); //SavePost한 후 postid를 반환

        //then
        //수정시에 내용이 안들어감  TODO: Global Exception
        System.out.println("updateRequest 내용 = " + updateRequest.getContent());
        System.out.println("updateRequest 내용 길이 = " + updateRequest.getContent().length());
//        assertThrows(NotFoundPostContentException.class, ()-> postService.updatePost(postid,user1Id,updateRequest));
//        assertThrows(NotFoundPostContentException.class, ()-> postService.updatePost(postid,user1Id,updateRequest2));
        assertThrows(ConstraintViolationException.class, ()-> postService.updatePost(postid,user1Id,updateRequest));
        assertThrows(ConstraintViolationException.class, ()-> postService.updatePost(postid,user1Id,updateRequest2));

    }

    /*************************************************************************************
     * 게시글 삭제 (Delete)
     **************************************************************************************/

    @Test
    @Rollback()
    @DisplayName("4 Delete-1 : DeletePost 성공 테스트")
    public void DeletePostTestToOK() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();

        UserDto.SaveRequest userDto2 = createUserDto2();
        User author2 = userDto2.toEntity();


        Long userId1 = userService.saveUser(userDto1);
        Long userId2 = userService.saveUser(userDto2);

        userRepository.save(author);
        userRepository.save(author2);

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title1")
                .content("content1")
                .category(cat)
                .build();

        PostDto.SaveRequest postDto2 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title2")
                .content("content2")
                .category(cat)
                .build();


        PostDto.SaveRequest postDto3 = PostDto.SaveRequest.builder() //다른 작성자
                .author(author2)
                .title("title3")
                .content("content3")
                .category(cat)
                .build();

        //when
        Long postid1 = postService.savePost(postDto1); //SavePost한 후 postid를 반환
        Long postid2 = postService.savePost(postDto2); //SavePost한 후 postid를 반환
        Long postid3 = postService.savePost(postDto3);

        //then
        //삭제 성공
        List<Post> all = postRepository.findAll();
        System.out.println("all = " + all);
        assertThat(postRepository.findAll().size()).isEqualTo(3); //저장 3개

        postService.deletePost(postid1,userId1); //3 - 1
        List<Post> all2 = postRepository.findAll();
        System.out.println("all2 = " + all2);
        assertThat(postRepository.findAll().size()).isEqualTo(2); //저장 2개로 변경

    }

    @Test
    @Rollback()
    @DisplayName("4 Delete-2 : DeletePost 예외 발생 테스트 - postid 존재하지 않음 ")
    public void DeletePostTestToFailByNullPostId() throws Exception{
        //given
        UserDto.SaveRequest userDto1 = createUserDto1();
        User author = userDto1.toEntity();

        Long userId1 = userService.saveUser(userDto1);

        Category root = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
        Long cid = categoryService.createCategory(categoryDto);
        Category cat = categoryRepository.findById(cid).get();

        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
                .author(author)
                .title("title1")
                .content("content1")
                .category(cat)
                .build();

        //when
        Long postid1 = postService.savePost(postDto1); //SavePost한 후 postid를 반환


        //then
        //삭제
        assertThrows(NotFoundPostByIdException.class, ()-> postService.deletePost(44L,userId1));

    }

// 프론트에서 해결
//    @Test
//    @Rollback()
//    @DisplayName("4 Delete-3 : DeletePost 예외 발생 테스트 - userid 존재하지 않음 ")
//    public void DeletePostTestToFailByNullAuthor() throws Exception{
//        //given
//        UserDto.SaveRequest userDto1 = createUserDto1();
//        User author = userDto1.toEntity();
//        Long userId1 = userService.saveUser(userDto1);
//
//        Category root = createRoot();
//        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
//        Long cid = categoryService.createCategory(categoryDto);
//        Category cat = categoryRepository.findById(cid).get();
//
//        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
//                .author(author)
//                .title("title1")
//                .content("content1")
//                .category(cat)
//                .build();
//
//        //when
//        Long postid1 = postService.savePost(postDto1); //SavePost한 후 postid를 반환
//
//
//        //then TODO: Global Exception
//        assertThrows(NotFoundUserException.class, ()-> postService.deletePost(postid1,44L));
//
//    }

    //프론트에서 해결
//    @Test
//    @Rollback()
//    @DisplayName("4 Delete-4 : DeletePost 예외 발생 테스트 - 삭제하려는 post가 본인의 게시글이 아님 ")
//    public void DeletePostTestToFailByNullPostInAuthor() throws Exception{
//        //given
//        UserDto.SaveRequest userDto1 = createUserDto1();
//        User author = userDto1.toEntity();
//        Long userId1 = userService.saveUser(userDto1);
//
//        UserDto.SaveRequest userDto2 = createUserDto2();
//        User author2 = userDto2.toEntity();
//        Long userId2 = userService.saveUser(userDto2);
//
//        Category root = createRoot();
//        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
//        Long cid = categoryService.createCategory(categoryDto);
//        Category cat = categoryRepository.findById(cid).get();
//
//        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
//                .author(author)
//                .title("title1")
//                .content("content1")
//                .category(cat)
//                .build();
//
//        //when
//        Long postid1 = postService.savePost(postDto1); //SavePost한 후 postid를 반환
//
//
//        //then  TODO: Global Exception
//        assertThrows(NotFoundPostInAuthorException.class, ()-> postService.deletePost(postid1,userId2));
//
//    }
}