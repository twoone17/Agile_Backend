package com.f3f.community.comment.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.comment.domain.Comment;
import static com.f3f.community.comment.dto.CommentDto.*;
import com.f3f.community.comment.repository.CommentRepository;
import com.f3f.community.exception.categoryException.MaxDepthException;
import com.f3f.community.exception.categoryException.NotEmptyChildCategoryException;
import com.f3f.community.exception.categoryException.NotFoundCategoryByIdException;
import com.f3f.community.exception.commentException.BanUserCommentException;
import com.f3f.community.exception.commentException.NotEmptyChildCommentException;
import com.f3f.community.exception.commentException.NotFoundCommentException;
import com.f3f.community.exception.commentException.NotFoundParentCommentException;
import com.f3f.community.exception.common.BannedUserException;
import com.f3f.community.exception.likeException.NotFoundLikesException;
import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.userException.NotFoundUserEmailException;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.likes.dto.LikesDto;
import com.f3f.community.likes.repository.LikesRepository;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.user.domain.BaseTimeEntity;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.domain.UserLevel;
import com.f3f.community.user.repository.UserRepository;
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.f3f.community.common.constants.ResponseConstants.*;

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
    public Long createComments(SaveRequest saveRequest){
        User author = userRepository.findByEmail(saveRequest.getAuthor().getEmail()).orElseThrow(NotFoundUserException::new);//유저가 있는지 확인하고 없으면 예외
        Post post = postRepository.findById(saveRequest.getPost().getId()).orElseThrow(NotFoundPostByIdException::new);//해당 게시글이 존재하지 않을 때

        //유저가 존재하면 이제 밴 당했는지의 여부를 확인.
        if(author.getUserLevel().equals(UserLevel.BAN)){
            throw new BanUserCommentException();
        }
//        //부모 댓글이 존재하지 않을 때,
//        if(saveRequest.getParentComment()==null){
//
//        } //--> 이게 필요한가..? CategoryName("root")을 대신할 것이 필요한가? 없음.

        Comment comment = saveRequest.toEntity();//엔티티 생성

        //부모 댓글이 비어있지 않고 부모 댓글이 이미 존재하면,
        if(comment.getParentComment()!=null && commentRepository.existsById(comment.getParentComment().getId())){
            Comment parent = commentRepository.findById(comment.getParentComment().getId()).get();
            if(parent.getDepth()>=1){
                throw new MaxDepthException();
            }
            parent.getChildComment().add(comment);
            comment.setDepth(parent.getDepth()+1);
        }
        post.getCommentList().add(comment);//게시글 댓글 리스트에 생성된 댓글 추가.
        commentRepository.save(comment);//댓글 저장

        return comment.getId();
    }

    //Read(게시글을 클릭할 때)
    @Transactional(readOnly = true)
    public List<Comment> findCommentsByPost(Post post){
        //게시글이 존재하는지를 확인
        if(!postRepository.existsById(post.getId())){
            throw new NotFoundPostByIdException();
        }
        return post.getCommentList();
    }

    //Read(유저를 클릭할 때)
    @Transactional(readOnly = true)
    public List<Comment> findCommentsByUser(User user){
        if(!userRepository.existsByEmail(user.getEmail())){
            throw new NotFoundUserEmailException();
        }
        User foundUser = userRepository.findById(user.getId()).get();
        if(foundUser.getUserLevel().equals(UserLevel.BAN)) {
            throw new BannedUserException();
        }

        return user.getComments();
    }



    //Update
    @Transactional
    public String updateComment(UpdateCommentRequest updateCommentRequest){
//        User author = userRepository.findByEmail(updateCommentRequest.Email).orElseThrow(NotFoundUserEmailException::new);
//        Post post = postRepository.findById(updateCommentRequest.postId).orElseThrow(NotFoundPostByIdException::new);
        Comment comment = commentRepository.findById(updateCommentRequest.getCommentId()).orElseThrow(NotFoundCommentException::new);
//        comment.getContent().replace(updateCommentRequest.getBeforeContent(), updateCommentRequest.getAfterContent());
        comment.updateContent(updateCommentRequest.getAfterContent());

        //부모 자식 확인할 필요없이 댓글 아이디로 수정하면 될듯.
        return UPDATE;
    }

    //Delete
    @Transactional
    public String deleteComments(Long Id){
        //User author = userRepository.findByEmail(comment.getAuthor().getEmail()).orElseThrow(NotFoundUserException::new);//유저가 있는지 확인하고 없으면 예외
         Comment comment = commentRepository.findById(Id).orElseThrow(NotFoundCommentException::new);
         Post post = postRepository.findById(comment.getPost().getId()).orElseThrow(NotFoundPostByIdException::new);//삭제하려는 게시글의 여부를 확인하고 없으면 예외

//        Comment parent = commentRepository.findById(comment.getParentComment().getId()).get();
        //댓글이 존재하는지 확인하고 존재하지 않으면 예외
//        if(!commentRepository.existsById(comment.getId())){
//            throw new NotFoundCommentByIdException();
//        }
        //-------부모 댓글을 지울 경우------------
        //대댓글이 없고 부모 댓글만 있다면 댓글 삭제(자식 댓글도 포함 될듯)
        if(comment.getChildComment().isEmpty()) {
            commentRepository.delete(comment);
        }
        else { //자식 댓글이 있다면, 자식 댓글 찾아서 지워야함.
            for(Comment childComment : comment.getChildComment()){
                commentRepository.delete(childComment);
            }
        }
        return DELETE;
    }


}
