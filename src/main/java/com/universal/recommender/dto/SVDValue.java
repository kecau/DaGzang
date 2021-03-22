package com.universal.recommender.dto;

import com.universal.recommender.enums.SVDAlgorithm;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SVDValue {

    private SVDAlgorithm svdAlgorithm;

    private Double value;

    private Integer minSvd;

    private Integer maxSvd;

    private Integer minIsvd;

    private Integer maxIsvd;
}
