package com.f3f.community.category.dto;

import com.f3f.community.category.domain.Category;
import com.f3f.community.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class CategoryDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SaveRequest{

        private String categoryName;

        private Category parents;

        private List<Category> childCategory = new ArrayList<>();

        private Long depth;

        private List<Post> postList = new ArrayList<>();

        public Category toEntity(){
            return Category.builder()
                    .categoryName(categoryName)
                    .parents(parents)
                    .childCategory(childCategory)
                    .depth(0L)
                    .postList(postList)
                    .build();
        }
    }


}
