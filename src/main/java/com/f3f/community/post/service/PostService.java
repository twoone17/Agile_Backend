package com.f3f.community.post.service;

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
    public Long savePost(@Valid PostDto.SaveRequest SaveRequest) throws Exception{ //SaveDto 활용

        Post post = SaveRequest.toEntity();
        postRepository.save(post);

        User author = post.getAuthor();
        //author의 postList에도 저장
        author.getPosts().add(post);
        //category의 postlist에 저장
        post.getCategory().getPostList().add(post);

        return post.getId();
    }

    /**
     * 게시글 조회 (Read)
     * a) return 값 : Post
     *  1.post_id로 post 찾기
     *
     * b) return 값 : PostList
     *  1) author로 postList 찾기
     *  2) title로 postList 찾기
     * -> 여러 게시글의 title이 똑같을 수 있다 : 우선 title과 완벽히 일치하는 postList 찾기 기능 구현
     *---------------------------------------------------------------------------------------
     * 추후 검색 class에서 구현해야 할 것들
     * 1) media로 postList 찾기
     * 2) title로 postList 찾기 : title 단어가 포함된 모든 postList 반환
     * 3) date로 postList 찾기
     * 4) content로 postList 찾기
     * 이외 여러가지 sort를 활용한 검색 기능
     */

//    Read a-1) post_id로 post 찾기
    @Transactional(readOnly = true)
    public Optional<Post> findPostByPostId(Long postId) throws Exception {
        //postRepository에 postid와 일치하는 게시글이 없으면 예외처리
        if(!postRepository.existsById(postId))
        {
            throw new NotFoundPostByPostIdException("postId와 일치하는 게시글이 없습니다");
        }
        //postRepository에 postId가 있을때
        Optional<Post> post = postRepository.findById(postId);
        return post;

    }

    // Read b-1) author로 postList 찾기
    @Transactional(readOnly = true)
    public List<Post> findPostListByAuthor(User author) throws Exception {
        //postRepository에 author와 일치하는 게시글이 없으면 예외처리
        if(!postRepository.existsByAuthor(author))
        {
            throw new NotFoundPostListByAuthor();
        }
        //postRepository에 author가 있을때
        List<Post> postList =  postRepository.findByAuthor(author);
        //author가 작성한 postlist를 반환
        return postList;

    }

    //Read b-2) title로 postList 찾기
    @Transactional(readOnly = true)
    public List<Post> findPostListByTitle(String title) throws Exception{
        //postRepository에 title과 일치하는 게시글이 없으면 예외처리
        if(!postRepository.existsByTitle(title))
        {
            throw new NotFoundPostListByTitle();
        }
        //postRepository에 title 있을때
        List<Post> postList =  postRepository.findByTitle(title);
        //title에 해당하는 postlist를 반환
        return postList;
    }

//    author - userId로 게시글을 찾을때 TODO: Author 자체로 찾으면 되는데, User 클래스 안에 있는 userId로 Post 서비스 단에서 굳이 찾을 필요가 있을까?
//    @Transactional(readOnly = true)
//    public List<Post> findPostListByUserId(Long userId) throws Exception {
//        if (postRepository.existsById(userId)) {
//            List<Post> postList =  postRepository.findPostListByUserId(userId); //postRepository에 userId가 있을때
//            return postList;
//        } else {
//            throw new NoPostByIdException("UserId와 일치하는 게시글리스트가 없습니다"); //postRepository에 userid로 저장된 게시글이 없으면 예외처리
//        }
//    }

    /**
     * 게시글 수정 (Update)
     * 변경 가능 값 : title, content, media
     * a) return 값 : String Ok
     *
     * 예외처리 )
     * 필수항목이 있는지
     * 1.postid 존재하는지
     * 2.title
     * 3.content
     */
    @Transactional
    public String updatePost(@Valid Long postId,Long userId, PostDto.UpdateRequest updateRequest) throws Exception{

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
     * a) return 값 : String Ok
     *
     * 예외처리 )
     * 본인 게시물인지 확인
     * 1.postid 존재하는지
     * 2.userid 존재하는지
     * 3.본인의 게시물인지 확인
     */
    @Transactional
    public String deletePost(Long postId, Long userId){
        Post post = postRepository.findById(postId).orElseThrow(NotFoundPostByIdException::new);
        postRepository.deleteById(postId); //postRepository에 있는 postId의 게시글을
        return "Delete OK";
    }



}