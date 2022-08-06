package com.f3f.community.comment.domain;

import com.f3f.community.media.domain.Media;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.BaseTimeEntity;
import com.f3f.community.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
    private List<Comment> childComment;

    @OneToMany(mappedBy = "comment" , fetch = FetchType.LAZY)
    private List<Media> mediaList;

    @Builder
    public Comment(Long id,String content, Post post, User author, Comment parentComment, List<Comment> childComment, List<Media> mediaList){
        this.content = content;
        this.post = post;
        this.author = author;
        this.parentComment = parentComment;
        this.childComment = childComment;
        this.mediaList = mediaList;
    }
}

