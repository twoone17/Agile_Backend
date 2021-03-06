package com.f3f.community.post.domain;


import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.media.domain.Media;
import com.f3f.community.post.dto.PostDto.SaveRequest;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
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
//@AllArgsConstructor 모든 필드 값을 파라미터로 받는 생성자를 만듦
//Builder 패턴을 사용, 빌더 메서드에만 @Builder 적용
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
    public Post(User author,String title,String content,List<Media> media,int viewCount,Scrap scrap,List<Comment> comments,List<Likes> likesList,List<PostTag> tagList)
    {
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

    //업데이트를 위한 메소드, title, content, media만 수정 가능
    public void updatePost(String title, String content, List<Media> media)
    {
        this.title = title;
        this.content = content;
        this.media = media;
    }


    public SaveRequest toDto(){
        return SaveRequest.builder()
                .author(this.author)
                .title(this.title)
                .content(this.content)
                .media(this.media)
                .viewCount(this.viewCount)
                .scrap(this.scrap)
                .comments(this.comments)
                .likesList(this.likesList)
                .tagList(this.tagList)
                .build();
    }




}
