package com.universal.recommender.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateParamDto {

    private String isUsingCustomData;

    private String selectedFileName;
}
