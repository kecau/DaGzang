package com.universal.recommender.repository;

import com.universal.recommender.model.DistributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DistributionRepository extends JpaRepository<DistributionEntity, Integer> {

    @Query("SELECT de FROM DistributionEntity de JOIN DistributionTypeEntity dte WHERE dte.name = :distributionTypeName")
    List<DistributionEntity> findAllByDistributionType(String distributionTypeName);

}
