package com.universal.recommender.service;

import com.universal.recommender.cache.DataGeneratedModel;
import com.universal.recommender.dto.GenerateParamDto;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public interface GeneratorService {

    DataTablesOutput<DataGeneratedModel> getGeneratedData(DataTablesInput dataTablesInput);

    ConcurrentMap<String, DataGeneratedModel> generateDataSample(GenerateParamDto generateParamDto);

    ByteArrayInputStream exportDataSample();
}
