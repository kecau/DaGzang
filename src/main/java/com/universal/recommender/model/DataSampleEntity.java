package com.universal.recommender.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "data_sample")
public class DataSampleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "rating")
    private String rating;
}
