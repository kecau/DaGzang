package com.universal.recommender.dto;

import com.universal.recommender.model.FileNameEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterSettingDto {

    private Integer userAmount;

    private Integer itemAmount;

    private Integer itemPerUserAmount;

    private Double ratingSparsity;

    private String selectedDistribution;

    private List<String> distributions;

    private String selectedFileName;

    private Boolean isUsingUserCommon;

    private List<String> fileNameList;

    private Integer numberOfCommonUser;
}
