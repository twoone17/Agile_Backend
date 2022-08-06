package com.f3f.community.comment.repository;

import com.f3f.community.admin.domain.Admin;
import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;


@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    boolean existsById(Long id);
    boolean existsByUser(User user);
    Optional<Comment> findById(Long id);
    Optional<Post> findById(Post post);


}
