package com.universal.recommender.repository;

import com.universal.recommender.model.RatingStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticRepository extends JpaRepository<RatingStatisticEntity, Integer> {
}
