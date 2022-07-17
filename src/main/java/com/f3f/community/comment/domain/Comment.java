package com.f3f.community.comment.domain;

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

    @OneToMany(mappedBy = "comment_id")
    private List<Comment> childComment;

    //미디어가 필요함
    //자식 commentList를 정렬 필요함 람다로 풀고
}
