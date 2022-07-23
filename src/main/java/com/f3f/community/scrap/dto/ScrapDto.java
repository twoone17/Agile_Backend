package com.f3f.community.scrap.dto;

import com.f3f.community.post.domain.Post;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import lombok.*;

import java.util.List;



public class ScrapDto {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SaveRequest{
        private Long scrapId;
        private String name;
        private User user;
        private List<Post> postList;

        public Scrap toEntity(){
            return Scrap.builder()
                    .scrapId(this.scrapId)
                    .name(name)
                    .user(user)
                    .postList(postList)
                    .build();
        }
    }



}
