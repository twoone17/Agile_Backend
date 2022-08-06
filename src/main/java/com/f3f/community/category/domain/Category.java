package com.f3f.community.category.domain;

import com.f3f.community.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private  String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parents;

    @OneToMany(mappedBy = "parents", fetch = FetchType.LAZY)
    private List<Category> childCategory = new ArrayList<>();

    private Long depth;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Post> postList = new ArrayList<>();

    @Builder
    public Category(String categoryName, Category parents, List<Category> childCategory, Long depth, List<Post> postList) {
        this.categoryName = categoryName;
        this.parents = parents;
        this.childCategory = childCategory;
        this.depth = depth;
        this.postList = postList;
    }

    public void setDepth(Long depth) {
        this.depth = depth;
    }


    public void updateName(String categoryName) {
        this.categoryName = categoryName;
    }


}
