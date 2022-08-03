package com.f3f.community.post.domain;

import com.f3f.community.scrap.domain.Scrap;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ScrapPost {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scrap_id")
    private Scrap scrap;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public ScrapPost(Scrap scrap, Post post) {
        this.scrap = scrap;
        this.post = post;
    }
}
