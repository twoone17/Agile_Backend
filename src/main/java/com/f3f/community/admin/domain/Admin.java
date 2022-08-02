package com.f3f.community.admin.domain;

import com.f3f.community.comment.domain.Comment;
import com.f3f.community.likes.domain.Likes;
import com.f3f.community.post.domain.Post;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.UserBase;
import com.f3f.community.user.domain.UserGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Admin extends UserBase {

    @Builder
    public Admin(Long id, String email, String password, String phone, UserGrade userGrade) {
        super(id, email, password, phone, userGrade);
    }

}
