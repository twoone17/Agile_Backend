package com.f3f.community.likes.repository;

import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes,Long> {
    boolean existsById(Long id);
    boolean existsByUser(User user);
    List<Likes> findByUser(User user);
    List<Likes> findByPost(Post post);

}
