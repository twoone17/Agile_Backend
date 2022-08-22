package com.f3f.community.tag.dto;

import com.f3f.community.tag.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


public class TagDto {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SaveRequest {

        @NotNull(message = "테그이름은 null이면 안됩니다.")
        private String tagName;

        public Tag toEntity() {
            return Tag.builder()
                    .tagName(tagName)
                    .build();
        }

    }

}
