package com.f3f.community.likes.service;

import com.f3f.community.exception.likeException.ExistLikeAlreadyException;
import com.f3f.community.exception.likeException.NotFoundLikesException;
import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.userException.NotFoundUserEmailException;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.likes.dto.LikesDto;
import com.f3f.community.likes.repository.LikesRepository;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.f3f.community.common.constants.ResponseConstants.OK;

/*
 1. C
     - post_id, user_id 확인하고 좋아요 생성
     - 더블 클릭 시 생성.
     - 한 사람 당 좋아요는 하나밖에 누를 수 없음.
     - 사용자가 차단 당한 사람인지 확인
 2. R
     - post_id를 확인하고 관련된 유저 리스트 출력
     - 좋아요 갯수 확인
 3. U
     - 좋아요 갯수?
 4. D
     - post_id, user_id 확인하고 본인 것만 삭제
     - likes_id를 보고 좋아요가 그 전에 생성되었었는지 확인.
     - 더블 클릭 시 삭제
  */

//TODO toEntity를 사용하는대신 DTO에 id 추가할지 다른 대안 생각.
@Service
@RequiredArgsConstructor
public class LikesService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;


    //create
    @Transactional
    public Long createLikes(LikesDto likesDto) {
        User user = userRepository.findByEmail(likesDto.getUser().getEmail()).orElseThrow(NotFoundUserEmailException::new);
        Post post = postRepository.findById(likesDto.getPost().getId()).orElseThrow(NotFoundPostByIdException::new);
        // DTO에서 ID를 받으면 DB값과 일치하지 않게 됨. toEntity로 객체를 만든 뒤 해당 객체의 id를 쓰겠음
        Likes likes = likesDto.toEntity();

        for (Likes like : post.getLikesList()) {
            if (likes.getId().equals(like.getId())) {
                Likes likes1 = likesRepository.findById(likes.getId()).orElseThrow(NotFoundLikesException::new);
                throw new ExistLikeAlreadyException();
            }
        }
        user.getLikes().add(likes);
        post.getLikesList().add(likes);
        likesRepository.save(likes);//좋아요 저장.
        return likes.getId();
    }

    //Delete
    @Transactional
    public String deleteLikes(LikesDto likesDto) {
        Post post = postRepository.findById(likesDto.getPost().getId()).orElseThrow(NotFoundPostByIdException::new);
        Likes toEntity = likesDto.toEntity();
        Likes likes = likesRepository.findById(toEntity.getId()).orElseThrow(NotFoundLikesException::new);
        for (Likes like : post.getLikesList()) {
            if (!likesDto.toEntity().getId().equals(like.getId())) {
                throw new NotFoundLikesException();
            }
        }
        likesRepository.delete(likes);
        return OK;
    }
}
