package com.f3f.community.post.repository;

import com.f3f.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Post findByIdPost(long id);

    boolean existsById(Long id);
}
