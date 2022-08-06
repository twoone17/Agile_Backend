package com.f3f.community.post.dto;

import com.f3f.community.category.domain.Category;
import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.media.domain.Media;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.PostTag;
import com.f3f.community.post.domain.ScrapPost;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

        @NotNull
        private User author;
        @NotNull
        @Size(min=2, max = 20, message = "title은 1~ 20자 이여야 합니다.")
        private String title;

        //필수값
        @NotNull
        @Size(min=1, message = "content는 1자 이상이어야 합니다.")
        private String content;

        private List<Media> media;

        private int viewCount;

        private Category category;

        private List<ScrapPost> scrapList = new ArrayList<>();

        private List<Comment> commentList = new ArrayList<>();

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
                    .category(this.category)
                    .scraps(this.scrapList)
                    .commentList(this.commentList)
                    .likesList(this.likesList)
                    .tagList(this.tagList)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @Size(min=2, message = "수정시 Title은 한글자 이상이어야 합니다.")
        private String title;

        @Size(min=2, message = "수정시 Content는 한글자 이상이어야 합니다.")
        private String content;

        private List<Media> media;

        public Post toEntity() {
            return Post.builder()
                    .title(this.title)
                    .content(this.content)
                    .media(this.media)
                    .build();
        }

    }

}
