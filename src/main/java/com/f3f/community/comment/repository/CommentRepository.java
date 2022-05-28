package com.f3f.community.comment.repository;

import com.f3f.community.admin.domain.Admin;
import com.f3f.community.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
}
