package com.universal.recommender.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rating_statistic")
public class RatingStatisticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "rating")
    private String rating;

    @Column(name = "amount")
    private Long amount;
}
