package com.f3f.community.tag.domain;


import com.f3f.community.post.domain.Post;
import com.f3f.community.tag.domain.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
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

    @Builder
    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }

}
