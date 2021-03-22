package com.universal.recommender.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataSampleDto {

    private String itemId;

    private String userId;

    private String rating;
}
