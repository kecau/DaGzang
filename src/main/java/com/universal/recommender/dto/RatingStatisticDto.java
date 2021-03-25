package com.universal.recommender.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingStatisticDto {

    private String id;

    private String rating;

    private Long amount;
}
