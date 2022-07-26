package com.f3f.community.post.service;

import com.f3f.community.exception.postException.NotFoundPostAuthorException;
import com.f3f.community.exception.postException.NotFoundPostContentException;
import com.f3f.community.exception.postException.NotFoundPostTitleException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    //final이나 @NonNull인 필드 값만 파라미터로 받는 생성자 만듦
    private final PostRepository postRepository;

    /**
     * 게시글 작성(Create)
     * 예외처리 )
     * a)필수항목이 있는지
     * 1.author
     * 2.title
     * 3.content
     * 4.category
     */
    @Transactional
    public Long SavePost(PostDto.SaveRequest SaveRequest) throws Exception{ //SaveDto 활용
        if(SaveRequest.getAuthor() ==null)
            throw new NotFoundPostAuthorException();
        if(SaveRequest.getTitle()==null)
            throw new NotFoundPostTitleException();
        if(SaveRequest.getContent()==null)
            throw new NotFoundPostContentException();
        //category는 추후에 추가예정
//        if(SaveDto.getCategory()==null)
//            throw new NotFoundPostCategoryException();

        Post post = SaveRequest.toEntity(); //SaveDto에서 entity로 바꿔준다

        postRepository.save(post); //저장
        return post.getId();
    }

    /**
     * 게시글 조회 (Read)
     */

    //게시글 id로 게시글을 찾을때 ㅇㅇㅇ
//    @Transactional(readOnly = true)
//    public Post getPostByPostId(Long postId) throws Exception {
//        if (postRepository.existsById(postId)) {
//            Post post = postRepository.findPostByPostId(postId);//postRepository에 userId가 있을때
//            return post;
//        } else {
//            throw new NoPostByIdException("postId와 일치하는 게시글이 없습니다"); //postRepository에 userid로 저장된 게시글이 없으면 예외처리
//        }
//    }
    //userId로 게시글을 찾을때
//    @Transactional(readOnly = true)
//    public List<Post> getPostListById(Long userId) throws Exception {
//        if (postRepository.existsById(userId)) {
//            List<Post> postList = postRepository.findPostListByUserId(userId); //postRepository에 userId가 있을때
//            return postList;
//        } else {
//            throw new NoPostByIdException("UserId와 일치하는 게시글리스트가 없습니다"); //postRepository에 userid로 저장된 게시글이 없으면 예외처리
//        }
//    }

//    //게시글 삭제
//    @Transactional
//    public void deletePost(Long postId){
//        postRepository.deleteById(postId); //postRepository에 있는 postId의 게시글을 지운다
//    }




    //모든 게시글 조회
//    public List<Post> findAllPosts() {
//        return postRepository.findAll();
//    }


}