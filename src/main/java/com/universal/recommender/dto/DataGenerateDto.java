package com.universal.recommender.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataGenerateDto {

    private List<DataSampleDto> dataSamples;
}
