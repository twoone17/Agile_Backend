package com.f3f.community.scrap.domain;


import com.f3f.community.post.domain.Post;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "scrap_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "scrap", fetch = LAZY)
    private List<Post> postList;


    public void updateScrap(String name){
        this.name = name;
//        this.user = user;
//        this.postList = postList;
    }

    public ScrapDto toDto() {
        return ScrapDto.builder()
                .id(id)
                .name(name)
                .user(user)
                .postList(postList)
                .build();
    }


}
