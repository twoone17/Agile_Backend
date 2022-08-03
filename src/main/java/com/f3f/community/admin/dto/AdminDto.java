package com.f3f.community.admin.dto;

import javax.validation.constraints.NotBlank;
import com.f3f.community.admin.domain.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public class AdminDto {

    @Getter
    @AllArgsConstructor
    public static class SaveRequest {
        @NotBlank
        private final Long id;

        public Admin toEntity() {
            return Admin.builder()
                    .id(this.id)
                    .build();
        }
    }
}
