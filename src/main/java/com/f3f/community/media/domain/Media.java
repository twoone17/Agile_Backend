package com.f3f.community.media.domain;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.rmi.activation.ActivationGroupDesc;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "media_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    //S3에 들어갈 미디어 경로
    private String mediaPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
