package com.f3f.community.comment.service;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.comment.dto.CommentDto;
import com.f3f.community.comment.repository.CommentRepository;
import com.f3f.community.likes.repository.LikesRepository;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.user.domain.BaseTimeEntity;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
댓글과 대댓글 구분하는거
 1. C
     - 포스트, 사용자 확인 -> 매개변수와 현재 포스트 아이디가 일치하는지
     - 게시물중에서 댓글 제한이 있는 포스트가 있는데 이건 뭐지?
     - 대댓글 - 상위가 맞는지, 1depth
     - 댓글 작성자가 유효한지(로그인이 되었는지)
 2. R
     - 특정 게시글에 대한 모든 댓글
 3. U
     - 수정하려는 댓글이 해당 댓글이 맞는지 (comment_id, user_id 둘 다 확인)
     - 선 댓글 후 차단 유저의 댓글은 어떻게 되나요?
     -
 4. D
     - 삭제하려는 댓글이 해당 댓글이 맞는지 (comment_id, user_id 둘 다 확인)
     - 관리자가 댓글 삭제 요청시, user_id, comment_id 확인하고 삭제
     - 사용자가 댓글을 고정할 수 있는 기능이 있나요?
     - 내가 parent 댓글이면 관련된 child 댓글은 모두 삭제.

  댓글에 좋아요 기능능

  */
@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;

    //Create
    @Transactional
    public Long createComments(CommentDto dto){
        User author = userRepository.findById(dto.getAuthor().getId()).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 유저입니다." + user_id));//밴 당했는지
        Post post = postRepository.findById(dto.getPost().getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다." + post_id));


        Comment comment = dto.toEntity();
        commentRepository.save(comment);

        return comment.getId();
    }

    @Transactional
    //Delete
    public String deleteComments(CommentDto dto){
        User author = userRepository.findById(dto.getAuthor().getId()).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 유저입니다." + user_id));//밴 당했는지
        Comment comment = postRepository.findById(dto.).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다." + post_id));
        commentRepository.delete(comment);

    }


}
