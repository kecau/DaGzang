package com.universal.recommender.model;

import com.universal.recommender.enums.RatingLevel;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "distribution")
public class DistributionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Double frequency;

    @Enumerated(EnumType.STRING)
    private RatingLevel ratingLevel;

    @ManyToOne
    @JoinColumn(name = "distribution_type_id")
    private DistributionTypeEntity distributionType;

}
