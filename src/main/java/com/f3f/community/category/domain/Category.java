package com.f3f.community.category.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id@GeneratedValue
    private Long id;

    private  String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> childCategory;

    @Builder
    public Category(String categoryName, Category parentCategory, List<Category> childCategory) {
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
        this.childCategory = childCategory;
    }
}
