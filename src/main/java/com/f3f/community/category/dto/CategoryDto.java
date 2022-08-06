package com.f3f.community.category.dto;

import com.f3f.community.category.domain.Category;
import com.f3f.community.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class CategoryDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SaveRequest{


        @NotNull(message = "카테고리 이름이 null 입니다.")
        @Size(min = 1, message = "카테고리 이름은 한 글자 이상이어야 합니다")
        private String categoryName;

        private Category parents;
        @NotNull(message = "자식 카테고리가 null 입니다.")
        private List<Category> childCategory = new ArrayList<>();

        @NotNull(message = "깊이 값이 null 입니다.")
        private Long depth;

        @NotNull(message = "포스트 리스트가 null 입니다.")
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
