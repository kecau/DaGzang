/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.universal.recommender.mapper;

import com.universal.recommender.dto.FrequencyDataDto;
import com.universal.recommender.dto.ParameterCustomDto;
import com.universal.recommender.enums.FrequencyType;
import com.universal.recommender.model.ParameterConfigEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.util.CollectionUtils;

/**
 *
 * @author KELAB
 */
public class ParameterMapper {
    
    public static ParameterCustomDto entityToDto(List<ParameterConfigEntity> parameterConfigEntities) {
        if (CollectionUtils.isEmpty(parameterConfigEntities)) {
            return null;
        }
        List<FrequencyDataDto> itemFrequencyDataDtos = new ArrayList<>();
        List<FrequencyDataDto> userFrequencyDataDtos = new ArrayList<>();
        List<FrequencyDataDto> ratingFrequencyDataDtos = new ArrayList<>();
        parameterConfigEntities.forEach(parameterConfigEntity -> {
            if (FrequencyType.ITEM.equals(parameterConfigEntity.getType())) {
                itemFrequencyDataDtos.add(buildFrequencyDataDto(parameterConfigEntity));
            } else if (FrequencyType.USER.equals(parameterConfigEntity.getType())) {
                userFrequencyDataDtos.add(buildFrequencyDataDto(parameterConfigEntity));
            } else if (FrequencyType.RATING.equals(parameterConfigEntity.getType())) {
                ratingFrequencyDataDtos.add(buildFrequencyDataDto(parameterConfigEntity));
            }
        });
        return ParameterCustomDto.builder()
                .item(itemFrequencyDataDtos)
                .user(userFrequencyDataDtos)
                .rating(ratingFrequencyDataDtos)
                .build();
    }
    
    private static FrequencyDataDto buildFrequencyDataDto(ParameterConfigEntity parameterConfigEntity) {
        return FrequencyDataDto.builder()
                .id(parameterConfigEntity.getId())
                .frequency(parameterConfigEntity.getFrequency())
                .value(parameterConfigEntity.getValue())
                .build();
    }
    
}
