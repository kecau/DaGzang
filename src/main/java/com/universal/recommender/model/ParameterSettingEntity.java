package com.universal.recommender.model;

import com.universal.recommender.enums.ParameterSetting;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parameter_setting")
public class ParameterSettingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "parameter_key")
    @Enumerated(EnumType.STRING)
    private ParameterSetting parameterKey;

    @Column(name = "parameter_value")
    private String parameterValue;
}
