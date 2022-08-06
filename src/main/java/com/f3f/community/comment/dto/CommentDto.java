package com.f3f.community.comment.dto;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.media.domain.Media;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDto {
    private String content;
    private Post post;
    private User author;

    private Comment parentComment;

    private List<Comment> childComment;

    private List<Media> mediaList;


    //DTO -> Entity
    public Comment toEntity(){
        return Comment.builder()
                .content(this.content)
                .post(this.post)
                .author(this.author)
                .parentComment(this.parentComment)
                .childComment(this.childComment)
                .mediaList(this.mediaList)
                .build();
    }
}
