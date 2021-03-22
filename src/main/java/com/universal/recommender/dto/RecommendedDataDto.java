package com.universal.recommender.dto;

import lombok.*;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedDataDto {

    private UUID id;

    private String itemId;

    private String userId;

    private String rating;

    public Double getRatingDouble() {
        if (StringUtils.isEmpty(rating)) {
            return 0d;
        }
        return Double.valueOf(rating);
    }
}
