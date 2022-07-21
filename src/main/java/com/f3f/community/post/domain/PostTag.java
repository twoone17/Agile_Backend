package com.f3f.community.post.domain;


import com.f3f.community.tag.domain.Tag;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;


    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
