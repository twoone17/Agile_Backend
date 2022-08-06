package com.f3f.community.likes.service;

import com.f3f.community.likes.repository.LikesRepository;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
public class LikesService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;

    private String createLikes()


}
