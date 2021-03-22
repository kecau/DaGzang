package com.universal.recommender.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaeDto {

    private DataGeneratedParamDto dataGeneratedParam;

    private String dataset;

    private Double value;
}
