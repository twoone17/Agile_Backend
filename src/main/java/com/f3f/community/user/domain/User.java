package com.f3f.community.user.domain;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.exception.userException.NicknameChangeConditionException;
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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Scrap> scraps = new ArrayList<>();

    private boolean isBanned;

    // List<List<Scrap>> 타입의 Collection 이라는 변수가 필요하다고 생각됨.

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        if(!undefinedCondition()) {
            this.nickname = nickname;
        } else {
            throw new NicknameChangeConditionException();
        }
    }

    private boolean undefinedCondition() {
        // 닉네임 변경 조건 정해지면 작성 예정
        return true;
    }

    public void banUser() {
        this.isBanned = true;
    }

    public void unBanUser() {
        this.isBanned = false;
    }

    @Builder
    public User(Long id, String email, String password, String phone, UserGrade userGrade,
                String nickname, String address, List<Post> posts, List<Comment> comments,
                List<Likes> likes, List<Scrap> scraps, boolean isBanned) {
        super(id, email, password, phone, userGrade);
        this.nickname = nickname;
        this.address = address;
        this.posts = posts;
        this.comments = comments;
        this.likes = likes;
        this.scraps = scraps;
        this.isBanned = isBanned;
    }
}
