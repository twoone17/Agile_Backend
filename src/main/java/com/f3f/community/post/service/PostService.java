package com.f3f.community.post.service;

import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    //게시글 작성
    @Transactional
    public void uploadPost(PostDto postDto){
        Post post = postDto.toEntity();
        postRepository.save(post);
    }
    //게시글 수정

    //게시글 삭제

    //게시글 검색

    //게시글 조회
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

//    public Post findOnePost(long postId) {
//        return postRepository.findOne(postId);}
//}
}