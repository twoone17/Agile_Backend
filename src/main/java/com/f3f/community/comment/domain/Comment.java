package com.f3f.community.comment.domain;

import com.f3f.community.media.domain.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @OneToMany(mappedBy = "comment_id", fetch = FetchType.LAZY)
    private List<Comment> childComment;

    @OneToMany(mappedBy = "comment" , fetch = FetchType.LAZY)
    private List<Media> mediaList;
}
