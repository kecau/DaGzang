/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.universal.recommender.service;

import com.universal.recommender.model.DataSampleEntity;
import com.universal.recommender.repository.DataSampleRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 *
 * @author KELAB
 */
@Service
public class DataSampleServiceImpl implements DataSampleService {
    
    private DataSampleRepository dataSampleRepository;
    
    public DataSampleServiceImpl(DataSampleRepository dataSampleRepository) {
        this.dataSampleRepository = dataSampleRepository;
    }

    @Override
    public void initeDataSample() {
        List<DataSampleEntity> dataSampleEntities = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            dataSampleEntities.add(DataSampleEntity.builder()
            .itemId("000" + i+1)
            .userId("000" + i+1)
            .rating("000" + i+1)
            .build());
        }
        dataSampleRepository.saveAll(dataSampleEntities);
    }
    
    
    
}
