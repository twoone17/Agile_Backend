package com.f3f.community.post.repository;

import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findPostsByAuthor(User user);

    Optional<Post> findById(Long id);

    boolean existsById(Long id);

}
