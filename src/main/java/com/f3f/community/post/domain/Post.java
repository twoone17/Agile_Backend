package com.f3f.community.post.domain;


import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.media.domain.Media;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    //필수값
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    //필수값
    private String title;

    //필수값
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

    @Builder
    public Post(Long id,
                User author,
                String title,
                String content,
                List<Media> media,
                int viewCount,
                Scrap scrap,
                List<Comment> comments,
                List<Likes> likesList,
                List<PostTag> tagList)
    {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.media = media;
        this.viewCount = viewCount;
        this.scrap = scrap;
        this.comments = comments;
        this.likesList = likesList;
        this.tagList = tagList;

    }

    public PostDto toDto(){
        return PostDto.builder()
                .id(id)
                .author(author)
                .title(title)
                .content(content)
                .media(media)
                .viewCount(viewCount)
                .scrap(scrap)
                .comments(comments)
                .likesList(likesList)
                .tagList(tagList)
                .build();
    }


}
