package com.f3f.community.user.domain;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.scrap.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends UserBase {

    private String nickname;

    private String address;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Scrap> scraps = new ArrayList<>();

    private UserLevel userLevel;

    private UserLogin userLogin;


    public void updatePassword(String password) {
        this.password = password;
    }

    // User 도메인에서는 서비스 로직 절대로 넣지 말자.
    // domain 클래스에는 특별한 기능이 들어가면 안됨. 그냥 데이터 처리? 만
    public void updateNickname(String nickname) {
            this.nickname = nickname;
    }


    public void updateUserGrade(int plusedKey) {
        this.userGrade = userGrade.getUserGradeByKey(plusedKey);
    }

    public void updateUserLevel(int key) {
        this.userLevel = userLevel.getUserLevelByKey(key);
    }


    @Builder
    public User(Long id, String email, String password, String phone, UserGrade userGrade,
                String nickname, String address, List<Post> posts, List<Comment> comments,
                List<Likes> likes, List<Scrap> scraps, UserLevel userLevel, UserLogin userLogin) {
        super(id, email, password, phone, userGrade);
        this.nickname = nickname;
        this.address = address;
        this.posts = posts;
        this.comments = comments;
        this.likes = likes;
        this.scraps = scraps;
        this.userLevel = userLevel;
        this.userLogin = userLogin;
    }
}
