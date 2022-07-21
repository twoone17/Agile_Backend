package com.f3f.community.scrap.dto;

import com.f3f.community.post.domain.Post;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ScrapDto {
    private Long id;
    private String name;
    private User user;
    private List<Post> postList;

    public Scrap toEntity(){
        return Scrap.builder()
                .id(this.id)
                .name(name)
                .user(user)
                .postList(postList)
                .build();
    }


}
