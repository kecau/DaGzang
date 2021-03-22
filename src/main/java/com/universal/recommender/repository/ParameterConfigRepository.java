package com.universal.recommender.repository;

import com.universal.recommender.model.DataSampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParameterConfigRepository  extends JpaRepository<DataSampleEntity, Long> {
}
