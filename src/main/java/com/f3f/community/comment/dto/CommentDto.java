package com.f3f.community.comment.dto;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.media.domain.Media;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentDto {
    @NotNull
    private String content;
    @NotNull
    private Post post;
    @NotNull
    private User author;
    private Comment parentComment;
    private List<Comment> childComment;
    private Long depth;
    private List<Media> mediaList;


    //DTO -> Entity
    public Comment toEntity(){
        return Comment.builder()
                .content(this.content)
                .post(this.post)
                .author(this.author)
                .parentComment(this.parentComment)
                .childComment(this.childComment)
                .depth(this.depth)
                .mediaList(this.mediaList)
                .build();
    }
    //생성용이 아니므로 필요한 애들만 가져와서 사용하기
    //업데이트 요청용.
    public static class UpdateCommentRequest{
        private String Email;
        private Long postId;
        private Long commentId;
        private String beforeContent;
        private String afterContent;
    }
}
