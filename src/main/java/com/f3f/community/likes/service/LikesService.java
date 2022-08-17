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
@Service
@RequiredArgsConstructor
@Transactional
public class LikesService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;


    //create
    private Long createLikes(LikesDto dto){
        User user = userRepository.findByEmail(dto.getUser().getEmail()).orElseThrow(NotFoundUserEmailException::new);
        //존재하지 않는 게시글이면 예외 처리
        Post post = postRepository.findById(dto.getPost().getId()).orElseThrow(NotFoundPostByIdException::new);

//        if(!userRepository.existsByEmail(dto.getUser().getEmail())) {//좋아요를 누를 때 존재하는 유저인지 확인 아니라면 예외 처리
//            throw new NotFoundUserEmailException();
//        }
        //좋아요가 이미 존재하는지 확인
        for( Likes like  : post.getLikesList() ){
            //유저가 이미 해당 게시물에 좋아요를 눌렀는지 확인
            if(dto.getId().equals(post.getLikesList())) {
                //Likes likes = likesRepository.findById(dto.getId()).orElseThrow(NotFoundLikesException::new);
                cancelLikes(dto);//리스트에 유저가 이미 존재, 두번 눌리면 취소 되어야함.
                throw new ExistLikeAlreadyException();
            }
        }
        Likes likes = dto.toEntity();//엔티티 생성
        likesRepository.save(likes);//좋아요 저장.
        user.getLikes().add(likes); //유저 좋아요 리스트에 추가
        post.getLikesList().add(likes);

        return likes.getId();
    }

    //Read
    //갯수에 대한 범위
    @Transactional(readOnly = true)
    public void totalLikes(Post post){
        return likesRepository.Count(post.getLikesList().get());
    }

    //Delete
    private String cancelLikes(LikesDto dto){
        User user = userRepository.findByEmail(dto.getUser().getEmail()).orElseThrow(NotFoundUserEmailException::new);
        Post post = postRepository.findById(dto.getPost().getId()).orElseThrow(NotFoundPostByIdException::new);
        Likes likes = likesRepository.findById(dto.getId()).orElseThrow(NotFoundLikesException::new);
        //게시글의 좋아요 목록과 좋아요 아이디를 보고
        for(Likes like : post.getLikesList()){
            if(!like.getId().equals(post.getLikesList())){ //만약 같은 아이디를 찾지 못하면 예외 처리 하고 좋아요 생성으로 감.
                throw new NotFoundLikesException();
                createLikes(dto);
            }
        }
        //유저 목록 빼야되나?

        likesRepository.delete(likes);
    }
