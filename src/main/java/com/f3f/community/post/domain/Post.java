package com.f3f.community.post.domain;


import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.media.domain.Media;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    private String title;

    private String content;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Media> media;

    private int viewCount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "scrap_id")
    private Scrap scrap;

    @OneToMany(mappedBy = "post", fetch = LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = LAZY)
    private List<Likes> likesList = new ArrayList<>();

    @OneToMany(mappedBy = "post",fetch = LAZY)
    private List<PostTag> tagList = new ArrayList<>();

}
