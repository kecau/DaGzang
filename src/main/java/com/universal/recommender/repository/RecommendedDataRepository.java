package com.universal.recommender.repository;

import com.universal.recommender.model.RecommendedDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedDataRepository extends JpaRepository<RecommendedDataEntity, Integer> {

    @Query("SELECT rd FROM RecommendedDataEntity rd ORDER BY rd.rating DESC")
    List<RecommendedDataEntity> findAllOrderByRatingDesc();
}
