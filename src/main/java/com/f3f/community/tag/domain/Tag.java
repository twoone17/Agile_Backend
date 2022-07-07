package com.f3f.community.tag.domain;

import com.f3f.community.post.domain.PostTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id@GeneratedValue
    private Long id;

    private String tagName;

    @OneToMany(mappedBy = "tag")
    private List<PostTag> memberProducts = new ArrayList<>();

}
