package com.universal.recommender.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "data_from_file")
public class DataFromFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "file_name_id")
    private FileNameEntity fileNameEntity;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "rating")
    private double rating;
}
