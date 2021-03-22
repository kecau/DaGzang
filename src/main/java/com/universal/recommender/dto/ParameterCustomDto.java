package com.universal.recommender.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterCustomDto {

    private List<FrequencyDataDto> item;

    private List<FrequencyDataDto> user;

    private List<FrequencyDataDto> rating;
}
