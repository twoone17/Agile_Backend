package com.f3f.community.likes.dto;

import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LikesDto {

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


