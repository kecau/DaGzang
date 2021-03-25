package com.universal.recommender.convert;

import com.universal.recommender.cache.DataGeneratedModel;
import com.universal.recommender.model.DataFromFileEntity;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class DataGeneratedConverter {

    public static ConcurrentMap<String, DataGeneratedModel> convertToDataSampleEntities(List<DataFromFileEntity> dataFromFileEntities) {
        if (CollectionUtils.isEmpty(dataFromFileEntities)) {
            return new ConcurrentHashMap<>();
        }

        return dataFromFileEntities.stream().collect(Collectors.toConcurrentMap(
                dataFromFileEntity -> DataGeneratedConverter.getIdFromDataFromFileEntity(dataFromFileEntity),
                dataFromFileEntity -> DataGeneratedConverter.convertToDataSampleEntity(dataFromFileEntity)
        ));
    }

    private static String getIdFromDataFromFileEntity(DataFromFileEntity dataFromFileEntity) {
        return String.valueOf(dataFromFileEntity.getId());
    }

    private static DataGeneratedModel convertToDataSampleEntity(DataFromFileEntity dataFromFileEntity) {
        return DataGeneratedModel.builder()
                .userId(dataFromFileEntity.getUserId())
                .itemId(dataFromFileEntity.getItemId())
                .rating(String.valueOf(dataFromFileEntity.getRating()))
                .build();
    }
}
