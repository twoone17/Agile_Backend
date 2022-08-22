package com.f3f.community.likes.dto;

import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


public class LikesDto {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SaveRequest {

        @NotNull
        private User user;
        @NotNull
        private Post post;

        public Likes toEntity() {
            return Likes.builder()
                    .user(user)
                    .post(post)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteLikes{
       // private User user;
        private Post post;
        private Long id;
    }

}


