package com.f3f.community.tag.domain;

import com.f3f.community.post.domain.PostTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String tagName;

    @OneToMany(mappedBy = "tag")
    private List<PostTag> memberProducts = new ArrayList<>();

}
