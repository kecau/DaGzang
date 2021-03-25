package com.universal.recommender.convert;

import com.universal.recommender.model.DataFromFileEntity;
import com.universal.recommender.model.DataSampleEntity;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultDataConverter {

    public static List<DataSampleEntity> convertToDataSampleEntities(List<DataFromFileEntity> dataFromFileEntities) {
        if (CollectionUtils.isEmpty(dataFromFileEntities)) {
            return Collections.emptyList();
        }
        return dataFromFileEntities.stream().map(dataFromFileEntity ->
                convertToDataSampleEntity(dataFromFileEntity)).collect(Collectors.toList());
    }

    public static DataSampleEntity convertToDataSampleEntity(DataFromFileEntity dataFromFileEntity) {
        return DataSampleEntity.builder()
                .userId(dataFromFileEntity.getUserId())
                .itemId(dataFromFileEntity.getItemId())
                .rating(String.valueOf(dataFromFileEntity.getRating()))
                .build();
    }
}
