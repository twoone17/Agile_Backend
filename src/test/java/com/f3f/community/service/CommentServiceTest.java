package com.f3f.community.service;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.comment.repository.CommentRepository;
import com.f3f.community.comment.service.CommentService;
import com.f3f.community.post.service.PostService;
import com.f3f.community.user.repository.UserRepository;
import com.f3f.community.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class CommentServiceTest {
    @Autowired CommentService commentService;
    @Autowired CommentRepository commentRepository;
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired PostService postService;
    @Autowired UserRepository postRepository;

    @Test
    public void 댓글작성() throws Exception {
        //given
        Comment comment = new Comment();
        comment.setContent("우와");

        //when
//         = commentService.join()

        //then
    }

    @Test
    public void 댓글삭제() throws Exception {
        //given

        //when

        //then
    }
}