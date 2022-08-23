package com.f3f.community.comment.repository;

import com.f3f.community.admin.domain.Admin;
import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment,Long> {

    boolean existsById(Long id);
    boolean existsByAuthor(User author);
    Optional<Comment> findById(Long id);
   // Optional<Post> findByPost(Post post);
    List<Comment> findByAuthor(User author);
    List<Comment> findByPost(Post post);
    List<Comment> findCommentsByParentComment (Comment parentComment);


}
