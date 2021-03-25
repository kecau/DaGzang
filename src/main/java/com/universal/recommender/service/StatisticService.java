package com.universal.recommender.service;

import com.universal.recommender.enums.RatingLevel;
import com.universal.recommender.model.RatingStatisticEntity;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface StatisticService {

    List<Long>  getAllRatingStatistics();

    void saveAllRatingStatistic(List<RatingStatisticEntity> ratingStatisticEntities);


    void saveAllRatingStatistic(ConcurrentMap<RatingLevel, Long> ratingStatistic);

    void deleteOldStatistic();
}
