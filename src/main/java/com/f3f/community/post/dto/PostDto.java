package com.f3f.community.post.dto;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.media.domain.Media;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.PostTag;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

//관심사 분리를 위한 DTO 클래스
//@Data
//@AllArgsConstructor
@Builder
public class PostDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveRequest {
        private Long id;
        private User author = new User();
        @NotNull
        private String title;

        //필수값
        @NotNull
        private String content;

        private List<Media> media;

        private int viewCount;

        private Scrap scrap;

        private List<Comment> comments = new ArrayList<>();

        private List<Likes> likesList = new ArrayList<>();

        private List<PostTag> tagList = new ArrayList<>();


        //각 dto마다 활용할 필드들이 다르기 때문에 각 필드를 Post 클래스를 거쳐 entity로 변환시켜준다
        //필드를 생성한다면, dto를 통하여 entity로 바꿔줘야함

        public Post toEntity() {
            return Post.builder()
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


}
