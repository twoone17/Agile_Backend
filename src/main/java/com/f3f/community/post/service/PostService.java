package com.f3f.community.post.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.exception.postException.*;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    /**
     * 게시글 작성(Create)
     */
    @Transactional
    public Long savePost(@Valid PostDto.SaveRequest SaveRequest) throws Exception{ //SaveDto 활용
        Post post = SaveRequest.toEntity();
        User author = userRepository.findById(post.getAuthor().getId()).get();
        author.getPosts().add(post);

        //category의 postlist에 저장
        Category category = categoryRepository.findById(post.getCategory().getId()).get();
        category.getPostList().add(post);
        postRepository.save(post);

        return post.getId();
    }

    /**
     * 게시글 조회 (Read)
     * a) return 값 : Post
     *  1.post_id로 post 찾기
     *
     * b) return 값 : PostList
     *  1) userid로 postList 찾기
     *  2) title로 postList 찾기
     * -> 여러 게시글의 title이 똑같을 수 있다 : 우선 title과 완벽히 일치하는 postList 찾기 기능 구현
     *---------------------------------------------------------------------------------------
     */

//    Read a-1) post_id로 post 찾기
    @Transactional(readOnly = true)
    public Optional<Post> findPostByPostId(Long postId) throws Exception {
        //postRepository에 postid와 일치하는 게시글이 없으면 예외처리
        if(!postRepository.existsById(postId)) {
            throw new NotFoundPostByPostIdException("postId와 일치하는 게시글이 없습니다");
        }
        //postRepository에 postId가 있을때
        Optional<Post> post = postRepository.findById(postId);
        return post;

    }
    //Read b-2) title로 postList 찾기
    @Transactional(readOnly = true)
    public List<Post> findPostListByTitle(String title) throws Exception{
        //postRepository에 title과 일치하는 게시글이 없으면 예외처리
        if(!postRepository.existsByTitle(title)) {
            throw new NotFoundPostListByTitle();
        }
        //postRepository에 title 있을때
        List<Post> postList =  postRepository.findByTitle(title);
        //title에 해당하는 postlist를 반환
        return postList;
    }

//    author - userId로 게시글을 찾을때
    @Transactional(readOnly = true)
    public List<Post> findPostListByUserId(Long userId) throws Exception {
        if (!postRepository.existsByAuthorId(userId)) {
            //postRepository에 userid로 저장된 게시글이 없으면 예외처리
            throw new NotFoundPostByUserIdException("UserId와 일치하는 게시글리스트가 없습니다");
        }

        List<Post> postList = postRepository.findByAuthorId(userId);//postRepository에 userId가 있을때
        return postList;
    }

    /**
     * 게시글 수정 (Update)
     * 변경 가능 값 : title, content, media
     */
    @Transactional
    public String updatePost(@Valid Long postId,@Valid Long userId,@Valid PostDto.UpdateRequest updateRequest) throws Exception{

        Post post = postRepository.findById(postId).orElseThrow(NotFoundPostByIdException::new);
        User author = userRepository.findById(userId).orElseThrow(NotFoundUserException::new);

        //post 업데이트
        post.updatePost(updateRequest);
        //postRepository에 업데이트 된 post 저장
        postRepository.save(post);
        //author의 postList에도 저장
        author.getPosts().add(post);

        return "OK";
    }


    /**
     * 게시글 삭제 (Delete)
     */
    @Transactional
    public String deletePost(Long postId, Long userId){
        Post post = postRepository.findById(postId).orElseThrow(NotFoundPostByIdException::new);
        postRepository.deleteById(postId); //postRepository에 있는 postId의 게시글을
        return "Delete OK";
    }



}