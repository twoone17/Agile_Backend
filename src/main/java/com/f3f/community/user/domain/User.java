package com.f3f.community.user.domain;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.scrap.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
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

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Scrap> scraps = new ArrayList<>();

    private UserLevel userLevel;



    public void updatePassword(String password) {
        this.password = password;
    }

    // User 도메인에서는 서비스 로직 절대로 넣지 말자.
    // domain 클래스에는 특별한 기능이 들어가면 안됨. 그냥 데이터 처리? 만
    public void updateNickname(String nickname) {
            this.nickname = nickname;
    }


//    public void updateUserGrade(int plusedKey) {
//        this.userGrade = userGrade.getUserGradeByKey(plusedKey);
//    }
//
//    public void updateUserLevel(int key) {
//        this.userLevel = userLevel.getUserLevelByKey(key);
//    }

    public void updateUserGrade(UserGrade usergrade) {
        super.userGrade = usergrade;
    }

    public void updateUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }
    public void updateAddress(String address) {
        this.address = address;
    }

    @Builder
    public User(Long id, String email, String password, String phone, UserGrade userGrade,
                String nickname, String address, List<Post> posts, List<Comment> comments,
                List<Likes> likes, List<Scrap> scraps, UserLevel userLevel) {
        super(id, email, password, phone, userGrade);
        this.nickname = nickname;
        this.address = address;
        this.posts = posts;
        this.comments = comments;
        this.likes = likes;
        this.scraps = scraps;
        this.userLevel = userLevel;
    }
}
