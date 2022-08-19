package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.categoryException.MaxDepthException;
import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.postException.NotFoundPostByPostIdException;
import com.f3f.community.exception.postException.NotFoundPostByUserIdException;
import com.f3f.community.exception.postException.NotFoundPostListByTitle;
import com.f3f.community.exception.scrapException.DuplicateScrapPostException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.ScrapPost;
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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(value = MethodOrderer.DisplayName.class)
class PostServiceTestWithDB {
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

    @Autowired
    ScrapPostService scrapPostService;


    private List<Long> createUsers(int n){
        List<Long> uids = new ArrayList<>();
        for(int i = 0; i<n; i++){
            UserDto.SaveRequest temp = createUserDto("guest" + i);
            Long uid = userService.saveUser(temp);
            uids.add(uid);
        }

        return uids;
    }
    private Long createRoot() throws Exception {
        CategoryDto.SaveRequest cat = createCategoryDto("root", null);
        Long rid = categoryService.createCategory(cat);
        return categoryRepository.findById(rid).get().getId();
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
            PostDto.SaveRequest postDto = createPostDto("title" + i, "content" + i, userRepository.findById(users.get(random.nextInt(users.size()))).get(), categoryRepository.findById(categories.get(random.nextInt(categories.size()))).get());
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

    private PostDto.SaveRequest createPostDto(String title,String content, User user, Category cat) {
        return PostDto.SaveRequest.builder()
                .title(title)
                .content(content)
                .author(user)
                .scrapList(new ArrayList<>())
                .category(cat).build();
    }

    @BeforeEach
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
        List<Long> users = createUsers(20);
        List<Long> categories = createCategories(3);
        List<Long> posts = createPosts(users, categories, 7);
        List<Long> scraps = createScraps(users, posts, 5);

        // when

        // then
    }

    @Test
    @DisplayName("디비에 데이터 들어가는 테스트")
    public void dbInsertionTest() throws Exception{
        //given
        UserDto.SaveRequest userSave = createUserDto("euisung");
        Long uid = userService.saveUser(userSave);
        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);
        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);
        ScrapDto.SaveRequest scrapDto = createScrapDto(userRepository.findById(uid).get(), "tempScrap");
        Long sid = scrapService.createScrap(scrapDto);

        // when
        scrapService.saveCollection(sid,uid, pid);
        List<Post> result = scrapPostService.getPostsOfScrap(sid);
        // then
        assertThat(result.get(0).getId()).isEqualTo(postRepository.findById(pid).get().getId());

    }


    /*************************************************************************************
     * 게시글 작성 테스트 (Create)
     **************************************************************************************/
    @Test
    @DisplayName("1 Save-1: savePost 성공 테스트")
    public void savePostTestWithDBToOk() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());

        //when
        Long pid = postService.savePost(postDto);

        //then

        postRepository.findById(pid).orElseThrow(NotFoundPostByIdException::new);
        assertThat(postRepository.findById(pid).get()).extracting("title","content").contains("title1","content1");
    }


    @Test
    @DisplayName("1 Save-2: savePost 여러개 성공 테스트")
    public void savePostTestWithDB_Mulitiple_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("user1");
        UserDto.SaveRequest userDto2 = createUserDto("user2");
        UserDto.SaveRequest userDto3 = createUserDto("user3");
        Long uid = userService.saveUser(userDto);
        Long uid2 = userService.saveUser(userDto2);
        Long uid3 = userService.saveUser(userDto3);


        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest postDto2 = createPostDto("title2","content2", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest postDto3 = createPostDto("title3","content3", userRepository.findById(uid2).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest postDto4 = createPostDto("title4","content4", userRepository.findById(uid3).get(), categoryRepository.findById(cid).get());

        //when
        Long pid = postService.savePost(postDto);
        Long pid2 = postService.savePost(postDto2);
        Long pid3 = postService.savePost(postDto3);
        Long pid4 = postService.savePost(postDto4);

        //then
        postRepository.findById(pid).orElseThrow(NotFoundPostByIdException::new);

//      저장한 post의 값 체크
        assertThat(postRepository.findAll())
                .extracting("title","content")
                .contains(tuple("title1","content1"),
                        tuple("title2","content2"),
                        tuple("title3","content3"),
                        tuple("title4","content4"));

        //저장한 게시글 4개인지 확인
        assertThat(postRepository.findAll().size()).isEqualTo(4);

    }

    @Test
    @DisplayName("1 Save-3: savePost 실패 테스트 - title 없음 ")
    public void savePostTestWithDBToFailByNullTitle() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        //when
        PostDto.SaveRequest postDto = createPostDto("","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());

        //then
        assertThatThrownBy(()->postService.savePost(postDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("title은 1~ 20자 이여야 합니다.");
    }

    @Test
    @DisplayName("1 Save-4: savePost 실패 테스트 - content 없음 ")
    public void savePostTestWithDBToFailByNullContent() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        //when
        PostDto.SaveRequest postDto = createPostDto("title1","", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());

        //then
        //TODO: controller에서 예외 처리
        assertThatThrownBy(()->postService.savePost(postDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("content는 1자 이상이어야 합니다.");

    }

    /*************************************************************************************
     * 게시글 조회 (Read)
     **************************************************************************************/

    @Test
    @DisplayName("2 Read-1 : findPostByPostId 성공 테스트")
    public void findPostByPostIdTestOk() throws Exception{
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        //when
        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());



        //when
        Long pid = postService.savePost(postDto);

        //then
        Post post = postService.findPostByPostId(pid).orElseThrow(NotFoundPostByPostIdException::new);
        assertThat(post).extracting("title","content").contains("title1","content1");
    }

    @Test
    @DisplayName("2 Read-2 : findPostByPostId 예외 발생 테스트 - postid 존재하지 않음 ")
    public void findPostByPostIdTestToFailByNullPostId() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        //when
        Long pid = postService.savePost(postDto);
        postService.deletePost(pid,uid);

        //then
        assertThatThrownBy(()->postService.findPostByPostId(pid))
                .isInstanceOf(NotFoundPostByPostIdException.class)
                .hasMessageContaining("postId와 일치하는 게시글이 없습니다");
    }

    @Test
    @DisplayName("2 Read-3 : findPostListByUserid 성공 테스트")
    public void findPostListByUseridTest_One_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);
        //when
        List<Post> postListByUserId = postService.findPostListByUserId(uid);

        //then
        //userid로 찾은 게시글이 잘 찾아졌는지 확인
        assertThat(postListByUserId.get(0)).extracting("title","content").contains("title1","content1");
    }

    @Test
    @DisplayName("2 Read-4 : findPostListByUserid 성공 테스트 : 여러개 ")
    public void findPostListByUseridTest_Multiple_Ok() throws Exception {
        //given
        UserDto.SaveRequest userDto = createUserDto("user1");
        UserDto.SaveRequest userDto2 = createUserDto("user2");
        UserDto.SaveRequest userDto3 = createUserDto("user3");
        Long uid = userService.saveUser(userDto);
        Long uid2 = userService.saveUser(userDto2);
        Long uid3 = userService.saveUser(userDto3);


        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        PostDto.SaveRequest postDto = createPostDto("title1", "content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest postDto2 = createPostDto("title2", "content2", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest postDto3 = createPostDto("title3", "content3", userRepository.findById(uid2).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest postDto4 = createPostDto("title4", "content4", userRepository.findById(uid3).get(), categoryRepository.findById(cid).get());

        //when
        Long pid = postService.savePost(postDto);
        Long pid2 = postService.savePost(postDto2);
        Long pid3 = postService.savePost(postDto3);
        Long pid4 = postService.savePost(postDto4);

        //then
        List<Post> postListByUserId = postService.findPostListByUserId(uid);

//      저장한 post의 값 체크
        assertThat(postListByUserId)
                .extracting("title", "content")
                .contains(tuple("title1", "content1"),
                        tuple("title2", "content2"))
                .doesNotContain(tuple("title3", "content3"),
                        tuple("title4", "content4"));

        //userid에 해당하는 게시글이 두개
        assertThat(postListByUserId.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("2 Read-4 : findPostListByUserid 예외 발생 테스트 - userid와 일치하는 게시글 없음")
    public void findPostListByUseridTestToFailByNullPostList() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        //when
        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);
        postService.deletePost(pid,uid);

        //then
        assertThatThrownBy(()->postService.findPostListByUserId(uid))
                .isInstanceOf(NotFoundPostByUserIdException.class)
                .hasMessageContaining("serId와 일치하는 게시글리스트가 없습니다");
    }

    @Test
    @DisplayName("2 Read-5 : findPostListByTitle 성공 테스트 (일치하는 title 하나) ")
    public void findPostListByTitleTest_One_Ok() throws Exception {
        //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        //when
        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);

        List<Post> postListBytitle = postService.findPostListByTitle("title1");//title에 해당하는 postList 찾기

        assertThat(postListBytitle.get(0)).extracting("title","content").contains("title1","content1");
    }

    @Test
    @DisplayName("2 Read-6 : findPostListByTitle 성공 테스트 (일치하는 title 여러개) ")
    public void findPostListByTitleTest_Multiple_Ok() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("user1");
        UserDto.SaveRequest userDto2 = createUserDto("user2");
        UserDto.SaveRequest userDto3 = createUserDto("user3");
        Long uid = userService.saveUser(userDto);
        Long uid2 = userService.saveUser(userDto2);
        Long uid3 = userService.saveUser(userDto3);


        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        PostDto.SaveRequest postDto = createPostDto("title1", "content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest postDto2 = createPostDto("title1", "content2", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest postDto3 = createPostDto("title1", "content3", userRepository.findById(uid2).get(), categoryRepository.findById(cid).get());
        PostDto.SaveRequest postDto4 = createPostDto("title4", "content4", userRepository.findById(uid3).get(), categoryRepository.findById(cid).get());

        //when
        Long pid = postService.savePost(postDto);
        Long pid2 = postService.savePost(postDto2);
        Long pid3 = postService.savePost(postDto3);
        Long pid4 = postService.savePost(postDto4);

        //then
        List<Post> postListBytitle = postService.findPostListByTitle("title1");

//      저장한 post의 값 체크
        assertThat(postListBytitle)
                .extracting("title", "content")
                .contains(tuple("title1", "content1"),
                        tuple("title1", "content2"),
                        tuple("title1", "content3"))
                .doesNotContain(tuple("title4", "content4"));

        //userid에 해당하는 게시글이 두개
        assertThat(postListBytitle.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("2 Read-7 : findPostListByTitle 예외 발생 테스트 - 일치하는 title 없음")
    public void findPostListByTitleTestToFailByNullTitle() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        //when
        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());
        Long pid = postService.savePost(postDto);
        postService.deletePost(pid,uid);

        assertThatThrownBy(()->postService.findPostListByTitle("title1"))
                .isInstanceOf(NotFoundPostListByTitle.class)
                .hasMessageContaining("PostList를 찾을 수 있는 title이 존재하지 않습니다");

    }

    /*************************************************************************************
     * 게시글 수정 (Update)
     **************************************************************************************/

    @Test
    @Rollback()
    @DisplayName("3 Update-1 : UpdatePost 성공 테스트")
    public void updatePostTestToOk() throws Exception{
        //given
        UserDto.SaveRequest userDto = createUserDto("euisung");
        Long uid = userService.saveUser(userDto);

        Long rid = createRoot();
        CategoryDto.SaveRequest categoryDto = createCategoryDto("kospi", categoryRepository.findById(rid).get());
        Long cid = categoryService.createCategory(categoryDto);

        PostDto.SaveRequest postDto = createPostDto("title1","content1", userRepository.findById(uid).get(), categoryRepository.findById(cid).get());

        PostDto.UpdateRequest updateRequest = PostDto.UpdateRequest.builder()
                .title("titleChanged")
                .content("contentChanged")
                .build();
        //when
        Long pid = postService.savePost(postDto);
        postService.updatePost(pid,uid,updateRequest);

        //then
        assertThat(postRepository.findAll()).extracting("title","content")
                .contains(tuple("titleChanged","contentChanged"))
                .doesNotContain(tuple("title1","content1"));

        }



}
