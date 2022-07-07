package com.f3f.community.post.domain;


import com.f3f.community.tag.domain.Tag;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PostTag {

    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;


    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
