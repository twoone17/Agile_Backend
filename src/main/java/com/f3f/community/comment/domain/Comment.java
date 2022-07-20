package com.f3f.community.comment.domain;

import com.f3f.community.media.domain.Media;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User author; // 댓글 도메인에 작성자 필드가 없는 것 같아서 추가했습니당


    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
    private List<Comment> childComment;

    @OneToMany(mappedBy = "comment" , fetch = FetchType.LAZY)
    private List<Media> mediaList;
}
