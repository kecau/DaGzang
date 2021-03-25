package com.universal.recommender.repository;

import com.universal.recommender.model.DataSampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataSampleRepository extends JpaRepository<DataSampleEntity, Long> {

    @Query("SELECT de FROM DataSampleEntity de ORDER BY de.rating DESC")
    List<DataSampleEntity> findAllOrderByRatingDesc();

}
