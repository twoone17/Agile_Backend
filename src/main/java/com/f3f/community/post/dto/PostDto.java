package com.f3f.community.post.dto;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.media.domain.Media;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.PostTag;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

//관심사 분리를 위한 DTO 클래스
@Data
@AllArgsConstructor
@Builder
public class PostDto {

    private Long id;
    private User author;
    private String title;

    //필수값
    private String content;

    private List<Media> media;

    private int viewCount;

    private Scrap scrap;

    private List<Comment> comments = new ArrayList<>();

    private List<Likes> likesList = new ArrayList<>();

    private List<PostTag> tagList = new ArrayList<>();


    public Post toEntity(){
        return Post.builder()
                .id(this.id)
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
