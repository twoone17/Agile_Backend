package com.f3f.community.scrap.domain;


import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.ScrapPost;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
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

    @NotNull
    @Size(min = 1,message = "이름은 한글자 이상이어야합니다")
    private String name;
    // NotNull

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    // NotNull

    @NotNull
    @OneToMany(mappedBy = "scrap", fetch = LAZY, cascade = CascadeType.REMOVE)
    private List<ScrapPost> postList = new ArrayList<>();
    // 생성될때는 빈 리스트

    @Builder
    public Scrap(Long id,String name, User user, List<ScrapPost> postList) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.postList = postList;
    }

    public void updateScrap(String name){
        this.name = name;
    }



}
