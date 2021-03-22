package com.universal.recommender.dto;

import com.universal.recommender.enums.SVDAlgorithm;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendParamDto {

    private String fileName;

    private SVDAlgorithm svdAlgorithm;
}
