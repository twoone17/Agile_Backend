package com.f3f.community.user.domain;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.scrap.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User extends UserBase {
    public User(Long id, String email, String password, String phone, UserGrade userGrade,
                String nickname, String address, List<Post> posts, List<Comment> comments,
                List<Likes> likes, List<Scrap> scraps) {
        super(id, email, password, phone, userGrade);
        this.nickname = nickname;
        this.address = address;
        this.posts = posts;
        this.comments = comments;
        this.likes = likes;
        this.scraps = scraps;
    }

    public User() {

    }

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

}
