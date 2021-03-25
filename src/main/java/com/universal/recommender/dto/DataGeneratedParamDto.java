package com.universal.recommender.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataGeneratedParamDto {

    private String fileName;

    private Double sparsity;

    private Integer ratingScale;

    private Integer userAmount;

    private Integer itemAmount;

    private Integer commonUser;

    private String distribution;
}
