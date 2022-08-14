package com.f3f.community.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.category.service.CategoryService;
import com.f3f.community.exception.userException.*;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.service.PostService;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.scrap.service.ScrapService;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserGrade;
import com.f3f.community.user.domain.UserLevel;
import com.f3f.community.user.domain.UserLogin;
import com.f3f.community.user.dto.UserDto;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.f3f.community.user.dto.UserDto.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ScrapRepository scrapRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    PostService postService;
    @Autowired
    ScrapService scrapService;

    private final String resultString = "OK";

    @BeforeEach
    public void delete() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        scrapRepository.deleteAll();
        postRepository.deleteAll();
    }

    private SaveRequest createUser() {
        SaveRequest userInfo = new SaveRequest("tempabc@tempabc.com", "ppadb123@", "01098745632", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH, "brandy", "pazu");
//        User user = userInfo.toEntity();
        return userInfo;
    }
    // 전달받은 매개변수를 유니크한 값으로 바꾼 user 엔티티를 저장한 뒤 반환한다.
    private SaveRequest createUserWithParams(String key) {
        SaveRequest userInfo;
        switch (key) {
            case "email" :
                userInfo = new SaveRequest("UniqueEmail@naver.com", "123456@qw", "01012345678", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH, "james", "changwon");
                break;
            case "password" :
                userInfo = new SaveRequest("temp@temp.com", "unique123@", "01012345678", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH,"james", "changwon");
                break;
            case "phone" :
                userInfo = new SaveRequest("temp@temp.com", "123456@qw", "uniquePhone", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH,"james", "changwon");
                break;
            case "nickname" :
                userInfo = new SaveRequest("temp@temp.com", "123456@qw", "01012345678", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH,"UniqueNickname", "changwon");
                break;
            default:
                userInfo = new SaveRequest("temp@temp.com", "123456@qw", "01012345678", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH,"james", "changwon");
                break;
        }
//        User user = userInfo.toEntity();
        return userInfo;
    }

    private SaveRequest createUserWithUniqueCount(int i) {
        SaveRequest userInfo = new SaveRequest("tempabc"+ i +"@tempabc.com", "ppadb123@" + i, "0109874563" + i, UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH, "brandy" + i, "pazu");
//        User user = userInfo.toEntity();
        return userInfo;
    }


//    private ScrapDto.SaveRequest createScrapDto1(User user) {
//        return ScrapDto.SaveRequest.builder()
//                .name("test")
//                .postList(new ArrayList<>())
//                .user(user)
//                .build();
//    }
//
//    private PostDto.SaveRequest createPostDto1(User user, Category cat) {
//        return PostDto.SaveRequest.builder()
//                .title("test title")
//                .content("test content for test")
//                .author(user)
//                .scrapList(new ArrayList<>())
//                .category(cat)
//                .build();
//    }
//
//    private PostDto.SaveRequest createPostDto2(User user, Category cat) {
//        return PostDto.SaveRequest.builder()
//                .title("test title2")
//                .content("test content for test2")
//                .author(user)
//                .scrapList(new ArrayList<>())
//                .category(cat)
//                .build();
//    }
//
//    private CategoryDto.SaveRequest createCategoryDto(String name, Category parent) {
//        return CategoryDto.SaveRequest.builder()
//                .categoryName(name)
//                .childCategory(new ArrayList<>())
//                .parents(parent)
//                .postList(new ArrayList<>()).build();
//    }
//
//    private Category createRoot() throws Exception{
//        CategoryDto.SaveRequest cat = createCategoryDto("root", null);
//        Long rid = categoryService.createCategory(cat);
//        return categoryRepository.findById(rid).get();
//    }

    @Test
    @DisplayName("회원가입 성공")
    public void userSaveTest() {
        // given
        SaveRequest userDTO = createUser();
        // when
        Long joinId = userService.saveUser(userDTO);
        Optional<User> byId = userRepository.findById(joinId);
        // then
        assertThat(byId.get().getId()).isEqualTo(joinId);
    }

    @Test
    @DisplayName("회원가입 실패 - 유효하지 않은 이메일")
    public void missingEmailInRegisterToFail() {
        //given
        SaveRequest saveRequest = new SaveRequest("", "1231", "01012345678", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH,"james", "here");

        //when & then
        assertThrows(ConstraintViolationException.class, () -> userService.saveUser(saveRequest));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효하지 않은 패스워드")
    public void missingPasswordInRegisterToFail() {
        //given
        SaveRequest saveRequest = new SaveRequest("temp@temp.com", "", "01012345678", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH,"james", "here");

        //when & then
        assertThrows(ConstraintViolationException.class, () -> userService.saveUser(saveRequest));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효하지 않은 닉네임")
    public void missingNicknameInRegisterToFail() {
        //given
        SaveRequest saveRequest = new SaveRequest("temp@temp.com", "1231", "01012345678", UserGrade.BRONZE, UserLevel.UNBAN, UserLogin.AUTH,"", "here");

        //when & then
        assertThrows(ConstraintViolationException.class, () -> userService.saveUser(saveRequest));
    }


    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    public void emailDuplicationToFailTest() {
        // given
        // 이메일 중복 시나리오
        SaveRequest user1DTO = createUserWithParams("nickname");
        SaveRequest user2DTO = createUserWithParams("phone");

        //when
        userService.saveUser(user1DTO);

        //then
        assertThrows(DuplicateEmailException.class, () -> userService.saveUser(user2DTO));
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    public void nicknameDuplicationTestToFail() {
        //given
        SaveRequest user1DTO = createUserWithParams("email");
        SaveRequest user2DTO = createUserWithParams("phone");
        //when
        userService.saveUser(user1DTO);

        //then
        assertThrows(DuplicateNicknameException.class, () -> userService.saveUser(user2DTO));
    }


    @Test
    @DisplayName("이메일, 패스워드 DTO로 회원정보 조회 성공")
    public void findUserByUserRequestDTOTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        UserRequest userRequest = new UserRequest(user.get().getEmail(), user.get().getPassword());
        User user1 = userService.findUserByUserRequest(userRequest);

        //then
        assertThat(user1.getEmail()).isEqualTo(user.get().getEmail());
    }

    @Test
    @DisplayName("이메일, 패스워드 DTO로 회원정보 조회 실패 - 존재하지 않는 패스워드 DTO로 회원정보 조회")
    public void findUserByUnknownPWUserRequestToFail() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        UserRequest userRequest = new UserRequest(user.get().getEmail(), "wrongpw123@");

        //then
        assertThrows(NotFoundPasswordException.class, () -> userService.findUserByUserRequest(userRequest));
    }

    @Test
    @DisplayName("이메일, 패스워드 DTO로 회원정보 조회 실패 - 존재하지 않는 이메일 DTO로 회원정보 조회")
    public void findUserByunknownEmailUserRequestToFail() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        UserRequest userRequest = new UserRequest("wrongEmail@xxx.com", user.get().getPassword());

        //then
        assertThrows(NotFoundUserException.class, () -> userService.findUserByUserRequest(userRequest));
    }

    @Test
    @DisplayName("닉네임으로 유저 조회 성공")
    public void findUserByNicknameTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        Long aLong1 = userService.findUserByNickname(user.get().getNickname());
        Optional<User> user1 = userRepository.findById(aLong1);

        //then
        assertThat(user1.get().getEmail()).isEqualTo(user.get().getEmail());
    }

    @Test
    @DisplayName("Id로 유저 조회 성공(내부 조회용)")
    public void findUserByIdTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        Long aLong1 = userService.findUserById(user.get().getId());
        Optional<User> user1 = userRepository.findById(aLong1);

        //then
        assertThat(user1.get().getId()).isEqualTo(user.get().getId());
    }

    @Test
    @DisplayName("Id로 유저 조회 실패 - 존재하지 않는 Id")
    public void findUserByNotFoundIdToFail() {
        //given
        Long NotFoundId = 32L;

        //when & then
        assertThrows(NotFoundUserException.class, () -> userService.findUserById(NotFoundId));
    }

    @Test
    @DisplayName("이메일로 유저 조회 성공")
    public void findUserByEmailTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
//        User userByEmail = userService.findUserByEmail(user.get().getEmail());
        Long aLong1 = userService.findUserByEmail(user.get().getEmail());
        Optional<User> userByEmail = userRepository.findById(aLong1);
        //then
        assertThat(userByEmail.get().getEmail()).isEqualTo(userDTO.getEmail());
    }

    @Test
    @DisplayName("이메일로 유저 조회 실패 - 존재하지 않는 이메일")
    public void findUserByNotFoundEmailToFail() {
        //given
        String notFoundEmail = "notFound";

        //when & then
        assertThrows(NotFoundUserException.class, () -> userService.findUserByEmail(notFoundEmail));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    public void changePasswordTest() {
        //given
        String newPW = "changed";
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(user.get().getEmail(), newPW, user.get().getPassword());
        String result = userService.updatePassword(changePasswordRequest);

        Optional<User> user2 = userRepository.findById(aLong);

        //then
        assertThat(result).isEqualTo(resultString);
        assertThat(user2.get().getPassword()).isEqualTo(newPW);
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 변경 전 비밀번호와 일치")
    public void changePasswordWithDuplicationToFail()  {
        // given - 이메일이 일치하는 유저가 있어야 하므로 먼저 유저를 생성.
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        // given - 그 후 위에서 생성한 유저의 이메일로 비밀번호 변경을 요청하겠다.
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest(user.get().getEmail(), user.get().getPassword(), user.get().getPassword());

        // when & then
        IllegalArgumentException e = assertThrows(DuplicateInChangePasswordException.class, () -> userService.updatePassword(changePasswordRequest));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 이메일 누락")
    public void changePasswordWithMissingEmailToFail() {
        //given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("12345789", "12345678abc@", "12345678a@");

        //when & then
        IllegalArgumentException e = assertThrows(NotFoundUserException.class, () -> userService.updatePassword(changePasswordRequest));

    }


    @Test
    @DisplayName("닉네임 변경 성공")
    public void changeNicknameTest() {
        //given
        String newNickname = "changed";
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        ChangeNicknameRequest changeNicknameRequest = new ChangeNicknameRequest(user.get().getEmail(), newNickname, user.get().getNickname());
        String result = userService.updateNickname(changeNicknameRequest);
        Optional<User> user2 = userRepository.findById(aLong);

        //then
        assertThat(result).isEqualTo(resultString);
        assertThat(user2.get().getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 변경 전 닉네임과 일치")
    public void changeNickname_DuplicationToFail()  {
        // given - 이메일이 일치하는 유저가 있어야 하므로 먼저 유저를 생성.
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        // given - 그 후 위에서 생성한 유저의 이메일로 닉네임 변경을 요청하겠다.
        ChangeNicknameRequest changeNicknameRequest = new ChangeNicknameRequest(user.get().getEmail(), user.get().getNickname(), user.get().getNickname());

        // when & then
        IllegalArgumentException e = assertThrows(DuplicateNicknameException.class, () -> userService.updateNickname(changeNicknameRequest));
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 존재하지 않는 이메일")
    public void changeNicknameWithMissingEmailToFail() {
        //given
        ChangeNicknameRequest changeNicknameRequest = new ChangeNicknameRequest("emptyEmail", "james", "michael");

        //when & then
        assertThrows(NotFoundUserException.class, () -> userService.updateNickname(changeNicknameRequest));
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 이미 존재하는 닉네임")
    public void changeNicknameToAlreadyExistsToFail() {
        //given
        SaveRequest user1DTO = createUserWithParams("email");
        SaveRequest user2DTO = createUser();
        Long aLong1 = userService.saveUser(user1DTO);
        Long aLong2 = userService.saveUser(user2DTO);
        Optional<User> user1 = userRepository.findById(aLong1);
        Optional<User> user2 = userRepository.findById(aLong2);

        //when
        ChangeNicknameRequest changeNicknameRequest = new ChangeNicknameRequest(user1.get().getEmail(), user2.get().getNickname(), user1.get().getNickname());

        //then
        assertThrows(DuplicateNicknameException.class, () -> userService.updateNickname(changeNicknameRequest));
    }


    @Test
    @DisplayName("회원탈퇴 성공")
    public void deleteUserTest() {
       //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        UserDeleteRequest userRequest = new UserDeleteRequest(user.get().getEmail(), user.get().getPassword(), user.get().getUserLogin());
        String result = userService.delete(userRequest);

        //then
        assertThat(result).isEqualTo(resultString);
        assertThat(userRepository.existsByEmail(userRequest.getEmail())).isEqualTo(false);
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 존재하지 않는 유저 이메일")
    public void deleteInvalidEmailUserToFail() {
        //given
        UserDeleteRequest userRequest = new UserDeleteRequest("invalidEmail@Email.com", "tempPW123@",UserLogin.AUTH);

        //when & then
        assertThrows(NotFoundUserException.class, () -> userService.delete(userRequest));
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 존재하지 않는 패스워드")
    public void deleteInvalidPasswordUserToFail() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        UserDeleteRequest userRequest = new UserDeleteRequest(user.get().getEmail(), "tempPW12@", user.get().getUserLogin());

        //then
        assertThrows(NotFoundPasswordException.class, () -> userService.delete(userRequest));
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 다른 유저의 패스워드")
    public void deleteOtherUserToFail() throws Exception {
        //given
        SaveRequest user1DTO = createUser();
        SaveRequest user2DTO = createUserWithUniqueCount(1);
        Long aLong1 = userService.saveUser(user1DTO);
        // user2의 패스워드는 ppadb1231 이다.
        Long aLong2 = userService.saveUser(user2DTO);

        Optional<User> user1 = userRepository.findById(aLong1);
        Optional<User> user2 = userRepository.findById(aLong2);

        //when
        // user1의 이메일, user2의 패스워드 모두 db에 존재하지만, 서로 매핑되지 않는 값이다.
        UserDeleteRequest userRequest = new UserDeleteRequest(user1.get().getEmail(), user2.get().getPassword(), user1.get().getUserLogin());

        //then
        assertThrows(NotMatchPasswordInDeleteUserException.class, () -> userService.delete(userRequest));
    }

    @Test
    @DisplayName("로그인 없이 비밀번호 변경 테스트")
    public void changePasswordWithoutSignInTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        ChangePasswordWithoutSignInRequest cpws = new ChangePasswordWithoutSignInRequest(user.get().getEmail(), "newPW123@");
        String result = userService.updatePasswordWithoutSignIn(cpws);
        Optional<User> user2 = userRepository.findById(aLong);

        //then
        assertThat(cpws.getAfterPassword()).isEqualTo(user2.get().getPassword());
        assertThat(result).isEqualTo(resultString);
    }

    @Test
    @DisplayName("비밀번호 찾기")
    public void findPasswordTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> user = userRepository.findById(aLong);

        //when
        SearchedPassword password = userService.findPassword(user.get().getEmail());

        //then
        assertThat(password.getPassword()).isEqualTo(user.get().getPassword());
    }

    // 프록시 초기화 문제로 테스트 불가.
//    @Test
//    @DisplayName("스크랩 목록 조회 테스트")
//    public void findScrapsOfUserTest() throws Exception {
//        //given
//        SaveRequest userDTO = createUser();
//        Long aLong = userService.saveUser(userDTO);
//        Optional<User> byId = userRepository.findById(aLong);
//
//        ScrapDto.SaveRequest scrap = createScrapDto1(byId.get());
//        Category root = createRoot();
//        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
//        Long cid = categoryService.createCategory(categoryDto);
//        Category cat = categoryRepository.findById(cid).get();
//        PostDto.SaveRequest post1 = createPostDto1(byId.get(), cat);
//        PostDto.SaveRequest post2 = createPostDto2(byId.get(), cat);
//
//        //when
//        Long pid1 = postService.savePost(post1);
//        Long pid2 = postService.savePost(post2);
//        Long sid = scrapService.createScrap(scrap);
//        scrapService.saveCollection(sid,aLong, pid1);
//        scrapService.saveCollection(sid,aLong, pid2);
//        List<Scrap> scrapsByUser = scrapRepository.findScrapsByUser(byId.get());
//
//        //then
//        assertThat(scrapsByUser.size()).isEqualTo(1);
//        assertThat(scrapsByUser.get(0).getPostList().size()).isEqualTo(2);
//    }

//    @Test
//    @DisplayName("유저가 작성한 게시글 조회 테스트")
//    public void findUserPostsByEmailTest() throws Exception {
//        //given
//        SaveRequest userDTO = createUser();
//        Long aLong = userService.saveUser(userDTO);
//        Optional<User> byId = userRepository.findById(aLong);
//
//        //when
//        Category root = createRoot();
//        CategoryDto.SaveRequest categoryDto = createCategoryDto("temp", root);
//        Long cid = categoryService.createCategory(categoryDto);
//        Category cat = categoryRepository.findById(cid).get();
//        PostDto.SaveRequest postDto1 = PostDto.SaveRequest.builder()
//                .author(byId.get())
//                .title("title")
//                .content("content1")
//                .category(cat)
//                .build();
//        postService.savePost(postDto1);
//
//        //then
//        assertThat(postRepository.existsByAuthor(byId.get())).isEqualTo(true);
//    }

    @Test
    @DisplayName("스크랩 목록 조회 테스트 - 비어있는 스크랩 목록")
    public void findEmptyScrapsOfUserTest() {
        //given
        SaveRequest userDTO = createUser();
        Long aLong = userService.saveUser(userDTO);
        Optional<User> byId = userRepository.findById(aLong);
        List<Scrap> emptyList = new ArrayList<>();
        //when
        List<Scrap> userScrapsByEmail = userService.findUserScrapsByEmail(byId.get().getEmail());

        //then
        assertThat(userScrapsByEmail).isEqualTo(emptyList);
    }
}