package com.f3f.community.post.repository;

import com.f3f.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findPostListByUserId(Long userid);

    Post findPostByPostId(Long postid);

    boolean existsById(Long id);

}
