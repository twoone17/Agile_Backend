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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "category_id")
    private Long id;

    private  String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categroy_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory" , fetch = FetchType.LAZY)
    private List<Category> childCategory;

    @Builder
    public Category(String categoryName, Category parentCategory, List<Category> childCategory) {
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
        this.childCategory = childCategory;
    }
}
