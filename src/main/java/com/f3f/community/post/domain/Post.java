package com.f3f.community.post.domain;


import com.f3f.community.category.domain.Category;
import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.media.domain.Media;
import com.f3f.community.post.dto.PostDto;
import com.f3f.community.post.dto.PostDto.SaveRequest;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.BaseTimeEntity;
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
public class Post extends BaseTimeEntity {

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
    private Category category;
    @OneToMany(mappedBy = "post", fetch = LAZY)
    private List<ScrapPost> scrapList = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = LAZY)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = LAZY)
    private List<Likes> likesList = new ArrayList<>();

    @OneToMany(mappedBy = "post",fetch = LAZY)
    private List<PostTag> tagList = new ArrayList<>();

    @Builder
    public Post(User author,String title,String content,List<Media> media,int viewCount,Category category,List<ScrapPost> scraps,List<Comment> commentList,List<Likes> likesList,List<PostTag> tagList)
    {
        this.author = author;
        this.title = title;
        this.content = content;
        this.media = media;
        this.viewCount = viewCount;
        this.category = category;
        this.scrapList = scraps;
        this.commentList = commentList;
        this.likesList = likesList;
        this.tagList = tagList;

    }

    //업데이트를 위한 메소드, title, content, media만 수정 가능
    public void updatePost(PostDto.UpdateRequest updateRequest)
    {
        this.title = updateRequest.getTitle();
        this.content = updateRequest.getContent();
        this.media = updateRequest.getMedia();
    }




    public SaveRequest toDto(){
        return SaveRequest.builder()
                .author(this.author)
                .title(this.title)
                .content(this.content)
                .media(this.media)
                .viewCount(this.viewCount)
                .scrapList(this.scrapList)
                .commentList(this.commentList)
                .likesList(this.likesList)
                .tagList(this.tagList)
                .build();
    }




}
