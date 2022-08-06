package com.f3f.community.likes.dto;

import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LikesDto {
    private Long id;
    private User user;
    private Post post;

    public Likes toEntity(Long id, User user, Post post) {
        return Likes.builder()
                .id(id)
                .user(user)
                .post(post)
                .build();
    }
}
