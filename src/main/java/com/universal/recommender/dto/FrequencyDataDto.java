package com.universal.recommender.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FrequencyDataDto {

    private Integer id;

    private Double frequency;

    private String value;
}
