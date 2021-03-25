package com.universal.recommender.repository;

import com.universal.recommender.model.DistributionTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistributionTypeRepository extends JpaRepository<DistributionTypeEntity, Integer> {

    Optional<DistributionTypeEntity> findByName(String name);
}
