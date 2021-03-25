package com.universal.recommender.service;

import com.universal.recommender.dto.MaeDto;
import com.universal.recommender.dto.RecommendParamDto;
import com.universal.recommender.dto.RecommendedDataDto;
import com.universal.recommender.model.RecommendedDataEntity;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface RecommenderService {

    void recommend();

    void calculateSvdAlgorithm(RecommendParamDto recommendParamDto);

    public void calculateMae();

    List<RecommendedDataEntity> saveRecommendedData(List<RecommendedDataEntity> recommendedDataEntities);

    void deleteAllRecommendedData();

    List<RecommendedDataDto> getAllRecommendedData();

    DataTablesOutput<RecommendedDataDto> getRecommendedData(DataTablesInput input);

    Double getMaeValue();

    List<MaeDto> getAllMaeValue();
}
