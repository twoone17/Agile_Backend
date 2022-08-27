package com.f3f.community.user.service;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.comment.repository.CommentRepository;
import com.f3f.community.exception.likeException.NotFoundLikesException;
import com.f3f.community.exception.postException.NotFoundPostListByAuthor;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.likes.repository.LikesRepository;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.repository.ScrapRepository;
import org.springframework.transaction.annotation.Transactional;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.exception.userException.*;
import org.springframework.stereotype.Service;
import com.f3f.community.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.f3f.community.common.constants.ResponseConstants.*;
import static com.f3f.community.common.constants.UserConstants.LIKE;
import static com.f3f.community.common.constants.UserConstants.VIEW;
import static com.f3f.community.user.dto.UserDto.*;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ScrapRepository scrapRepository;
    private final LikesRepository likesRepository;
    @Transactional
    public Long saveUser(@Valid SaveRequest saveRequest) {


        if(userRepository.existsByEmail(saveRequest.getEmail())) {
            throw new DuplicateEmailException();
        }
        if(userRepository.existsByNickname(saveRequest.getNickname())) {
            throw new DuplicateNicknameException();
        }

        User user = saveRequest.toEntity();
        User saveUser = userRepository.save(user);
        return saveUser.getId();
    }

    @Transactional
    public String updateNickname(@Valid ChangeNicknameRequest changeNicknameRequest) {
        String email = changeNicknameRequest.getEmail();
        String beforeNickname = changeNicknameRequest.getBeforeNickname();
        String afterNickname = changeNicknameRequest.getAfterNickname();


        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);


        if(userRepository.existsByNickname(afterNickname)) {
            throw new DuplicateNicknameException();
        }

        if(beforeNickname.equals(afterNickname)) {
            throw new DuplicateNicknameException();
        }

        user.updateNickname(afterNickname);
        return OK;
    }

    @Transactional
    public String updatePassword(@Valid ChangePasswordRequest changePasswordRequest) {
        String email = changePasswordRequest.getEmail();
        String beforePassword = changePasswordRequest.getBeforePassword();
        String afterPassword = changePasswordRequest.getAfterPassword();


        User user = userRepository.findByEmail(changePasswordRequest.getEmail()).orElseThrow(NotFoundUserException::new);

        if(beforePassword.equals(afterPassword)) {
            throw new DuplicateInChangePasswordException();
        }

        user.updatePassword(afterPassword);

        return OK;
    }

    //TODO 비밀번호 분실 시, 기존 비밀번호를 다시 알려줄지 초기화로 다시 설정하게 할지 고민하다
    //  두 기능 모두 구현해둠.

    @Transactional
    public String updatePasswordWithoutSignIn(@Valid ChangePasswordWithoutSignInRequest request) {
        String email = request.getEmail();
        String AfterPassword = request.getAfterPassword();

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(NotFoundUserException::new);

        // TODO 이메일 인증
        CertificateEmail(user.getEmail());
        user.updatePassword(AfterPassword);
        return OK;
    }

    @Transactional
    public SearchedPassword findPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);
        // TODO 이메일 인증
        CertificateEmail(user.getEmail());

        // TODO 암호화?
        String EncryptPW = user.getPassword();
        SearchedPassword searchedPassword = new SearchedPassword(EncryptPW);
        return searchedPassword;
    }



    @Transactional
    public String delete(@Valid UserDeleteRequest userRequest) {

        //TODO: 로그인 여부(나중에), password 암호화
        // 이메일 검증, 본인의 이메일임을 검증해야함

        User user = userRepository.findByEmail(userRequest.getEmail()).orElseThrow(NotFoundUserException::new);

        if(!userRepository.existsByPassword(userRequest.getPassword())) {
            throw new NotFoundPasswordException();
        }

        if(!user.getPassword().equals(userRequest.getPassword())) {
            throw new NotMatchPasswordInDeleteUserException();
        }

        userRepository.deleteByEmail(userRequest.getEmail());
        return OK;
    }


    @Transactional(readOnly = true)
    public User findUserByUserRequest(@Valid UserRequest userRequest) {
        if(!userRepository.existsByPassword(userRequest.getPassword())) {
            throw new NotFoundPasswordException();
        }
        User user = userRepository.findByEmail(userRequest.getEmail()).orElseThrow(NotFoundUserException::new);
        return user;
    }

    @Transactional(readOnly = true)

    public Long findUserByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(NotFoundNicknameException::new);
        return user.getId();
    }

    @Transactional(readOnly = true)
    public Long findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(NotFoundUserException::new);
        return user.getId();
    }

    @Transactional(readOnly = true)
    public Long findUserByEmail(@NotBlank String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);
        return user.getId();
    }

    // 유저 조회했을때 post를 다 보여줄거냐, 10개로 끊어서 보여줄지,
    // scrap도 마찬가지, user는 정보를 다 가지고 있어서 이걸 어떻게 뿌릴지 고민해야한다.

    // 특정 유저가 작성한 게시글은 postService에서 제공하는 듯 하지만 유저에서도 별도로 제공해야한다고 판단하여
    // 게시글, 댓글 조회 기능을 구현하겠다.

    @Transactional(readOnly = true)
    public List<Post> findUserPostsByEmail(@NotBlank String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);
        if(!postRepository.existsByAuthor(user)) {
            throw new NotFoundPostListByAuthor();
        }
        List<Post> postList =  postRepository.findByAuthor(user);
        return postList;
    }

    @Transactional(readOnly = true)
    public List<Scrap> findUserScrapsByEmail(@NotBlank String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);
        List<Scrap> scrapsByUser = scrapRepository.findScrapsByUser(user);
        return scrapsByUser;
    }

    @Transactional(readOnly = true)
    public List<Post> findUserPostsWithOptions(@Valid MyPageRequest myPageRequest) {
        User user = userRepository.findByEmail(myPageRequest.getEmail()).orElseThrow(NotFoundUserException::new);
        if(!postRepository.existsByAuthor(user)) {
            throw new NotFoundPostListByAuthor();
        }
        List<Post> posts;
        switch (myPageRequest.getOption()) {
            case VIEW:
                posts = postRepository.findByAuthorOrderByViewCountDesc(user);
                break;

            case LIKE:
                List<Post> temp = postRepository.findByAuthor(user);
                Collections.sort(temp, new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        List<Likes> post1 = likesRepository.findByPost(o1);
                        List<Likes> post2 = likesRepository.findByPost(o2);
                        if(post1.size() > post2.size()) {
                            return -1;
                        } else if(o1.getLikesList().size() < o2.getLikesList().size()) {
                            return 1;
                        }
                        return 0;
                    }
                });
                posts = temp;
                break;

            default:
                posts = postRepository.findByAuthor(user);
                break;
        }
        int slice = Math.min(myPageRequest.getLimit(), posts.size());
        return new ArrayList<>(posts.subList(0, slice));
    }

    @Transactional(readOnly = true)
    public List<Comment> findUserCommentsByEmail(@NotBlank String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);
        List<Comment> comments = commentRepository.findByAuthor(user);
        if(comments.isEmpty()) {
            throw new NotFoundUserEmailException();
        }
        return comments;
    }

    @Transactional(readOnly = true)
    public List<Comment> findUserCommentsWithLimitByEmail(@Valid GetCommentRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(NotFoundUserException::new);
        List<Comment> comments = commentRepository.findByAuthor(user);
        if(comments.isEmpty()) {
            throw new NotFoundUserEmailException();
        }
        int slice = Math.min(request.getLimit(), comments.size());
        return new ArrayList<>(comments.subList(0, slice));
    }

    @Transactional
    public String updateUserAddressInfo(@Valid UpdateUserAddressRequest request) {
        User user = userRepository.findByEmail(request.getUser().getEmail()).orElseThrow(NotFoundUserException::new);
        if (user.getAddress().equals(request.getAddress())) {
            throw new DuplicateChangeInfoException();
        }
        user.updateAddress(request.getAddress());
        return OK;
    }

    @Transactional
    public String updateUserPhoneInfo(@Valid UpdateUserPhoneRequest request) {
        User user = userRepository.findByEmail(request.getUser().getEmail()).orElseThrow(NotFoundUserException::new);
        if (user.getPhone().equals(request.getPhone())) {
            throw new DuplicateChangeInfoException();
        }
        user.updatePhone(request.getPhone());
        return OK;
    }

    @Transactional(readOnly = true)
    public List<Likes> findUserLikesByEmail(@NotBlank String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);
        List<Likes> byUser = likesRepository.findByUser(user);
        if(byUser.isEmpty()) {
            throw new NotFoundLikesException();
        }
        return byUser;
    }


    private boolean CertificateEmail(String email) {
        // TODO 임시 조건.
        if(true) {

        } else {
            throw new CertificateEmailException();
        }
        return true;
    }

}
