package com.f3f.community.post.service;

import com.f3f.community.exception.postException.*;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    //final이나 @NonNull인 필드 값만 파라미터로 받는 생성자 만듦
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
        User author = post.getAuthor();
        postRepository.save(post); //postRepository에 저장
        author.getPosts().add(post); //author의 postList에도 저장
        userRepository.save(author); //userRepository에 postlist가 추가된 author 저장

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
        if (postRepository.existsById(postId)) {
            Optional<Post> post = postRepository.findById(postId);//postRepository에 postId가 있을때
            return post;
        } else {
            throw new NotFoundPostByPostIdException("postId와 일치하는 게시글이 없습니다"); //postRepository에 postId로 저장된 게시글이 없으면 예외처리
        }
    }

    // Read b-1) author로 postList 찾기
    @Transactional(readOnly = true)
    public List<Post> findPostListByAuthor(User author) throws Exception {
        if (postRepository.existsByAuthor(author)) {
            List<Post> postList =  postRepository.findByAuthor(author); //postRepository에 author가 있을때
            return postList; //author가 작성한 postlist를 반환
        } else {
            throw new NotFoundPostListByAuthor(); //postRepository에 author가 작성한 게시글이 없으면 예외처리
        }
    }

    //Read b-2) title로 postList 찾기
    @Transactional(readOnly = true)
    public List<Post> findPostListByTitle(String title) throws Exception{
        if (postRepository.existsByTitle(title)) {
            List<Post> postList =  postRepository.findByTitle(title); //postRepository에 title 있을때
            return postList; //title에 해당하는 postlist를 반환
        } else {
            throw new NotFoundPostListByTitle(); //postRepository에 title과 일치하는 게시글이 없으면 예외처리
        }
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
    public String updatePost(Long postId,Long userId, PostDto.UpdateRequest updateRequest) throws Exception{ //UpdateDto 활용

        Post post = postRepository.findById(postId).orElseThrow(NotFoundPostByIdException::new);
        User author = userRepository.findById(userId).orElseThrow(NotFoundUserByIdException::new);
        List<Post> author_posts = author.getPosts();
        if(!author_posts.contains(post))
        {
            throw new NotFoundPostInAuthorException("본인 게시물이 아닌 다른 사람의 게시물을 수정할 수 없습니다");
        }
        if(updateRequest.getTitle() == null || updateRequest.getTitle().length()<1)
            throw new NotFoundPostTitleException("수정시 Title은 한글자 이상이어야 합니다.");
        if(updateRequest.getContent() == null || updateRequest.getContent().length()<1)
            throw new NotFoundPostContentException("수정시 Content는 한글자 이상이어야 합니다.");
        post.updatePost(updateRequest.getTitle(), updateRequest.getContent(),updateRequest.getMedia());

        postRepository.save(post); //postRepository에 저장
//        System.out.println("author = " + author);
//        System.out.println("author.getPosts() = " + author.getPosts());
//        author.getPosts().add(post); //author의 postList에도 저장 , author의 postlist에는 update할때 추가로 저장 안해도 되나?
//        userRepository.save(author); //userRepository에 postlist가 추가된 author 저장

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
        User author = userRepository.findById(userId).orElseThrow(NotFoundUserByIdException::new);
        List<Post> author_posts = author.getPosts();
        if(!author_posts.contains(post))
        {
            throw new NotFoundPostInAuthorException("본인 게시물이 아닌 다른 사람의 게시물을 삭제할 수 없습니다");
        }
        postRepository.deleteById(postId); //postRepository에 있는 postId의 게시글을
        return "Delete OK";
    }



}