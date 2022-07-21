package com.f3f.community.scrap.dto;

import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class ScrapDto {
    private Long id;
    private String name;
    private User user;
    private List<Post> postList;

}
