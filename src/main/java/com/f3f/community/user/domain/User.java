package com.f3f.community.user.domain;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.scrap.domain.Scrap;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User extends UserBase {
    private String nickname;

    private String address;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Scrap> scraps = new ArrayList<>();

    @Builder
    public User(Long id, String email, String password, String phone, UserGrade userGrade,
                String nickname, String address) {
        super(id, email, password, phone, userGrade);
        this.nickname = nickname;
        this.address = address;
        this.posts = null;
        this.comments = null;
        this.likes = null;
        this.scraps = null;
    }
}
