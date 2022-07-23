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
@NoArgsConstructor
public class Scrap {

    @Id
    @GeneratedValue
    @Column(name = "scrap_id")
    private Long id;

    private String name;
    // NotNull

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    // NotNull

    @OneToMany(mappedBy = "scrap", fetch = LAZY)
    private List<Post> postList;
    // 생성될때는 빈 리스트

    @Builder
    public Scrap(Long id,String name, User user, List<Post> postList) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.postList = postList;
    }

    public void updateScrap(String name){
        this.name = name;
//        this.user = user;
//        this.postList = postList;
    }



}
