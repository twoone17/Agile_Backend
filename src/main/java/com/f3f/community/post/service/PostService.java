package com.f3f.community.post.service;

import com.f3f.community.exception.postException.NoPostByIdException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    //게시글 작성 , 게시글 수정
    @Transactional
    public void uploadPost(PostDto postDto){
        Post post = postDto.toEntity();
        postRepository.save(post);
    }
    
    //게시글 삭제
    @Transactional
    public void deletePost(Long postId){
        postRepository.deleteById(postId); //postRepository에 있는 postId의 게시글을 지운다
    }

    //게시글 조회
    //userId로 게시글을 찾을때
    @Transactional(readOnly = true)
    public List<Post> getPostListById(Long userId) throws Exception {
        if (postRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            List<Post> postList = postRepository.findPostsByAuthor(user); //postRepository에 userId가 있을때
            return postList;
        } else {
            throw new NoPostByIdException("UserId와 일치하는 게시글리스트가 없습니다"); //postRepository에 userid로 저장된 게시글이 없으면 예외처리
        }
    }
    
    //게시글 id로 게시글을 찾을때

    @Transactional(readOnly = true)
    public Post getPostByPostId(Long postId) throws Exception {
        if (postRepository.existsById(postId)) {
            Post post = postRepository.findById(postId).get();//postRepository에 userId가 있을때
            return post;
        } else {
            throw new NoPostByIdException("postId와 일치하는 게시글이 없습니다"); //postRepository에 userid로 저장된 게시글이 없으면 예외처리
        }
    }

    //모든 게시글 조회
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }


}