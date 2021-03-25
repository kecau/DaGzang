package com.universal.recommender.convert;

import com.universal.recommender.cache.DataGeneratedModel;
import com.universal.recommender.enums.RatingLevel;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class RatingStatisticConvert {

    public static ConcurrentMap<RatingLevel, Long> fromGeneratedDataToRatingStatistic(
            ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap) {
        return dataGeneratedMap.values().stream().collect(Collectors.groupingByConcurrent(
                dataGeneratedModel -> getRating(dataGeneratedModel.getRating()),
                Collectors.counting()));
    }

    private static RatingLevel getRating(String rating) {
        if (StringUtils.isEmpty(rating)) {
            return RatingLevel.EMPTY;
        }
        if (Double.valueOf(rating).equals(Double.valueOf(RatingLevel.RATING_1.getValue()))) {
            return RatingLevel.RATING_1;
        } else if (Double.valueOf(rating).equals(Double.valueOf(RatingLevel.RATING_2.getValue()))) {
            return RatingLevel.RATING_2;
        } else if (Double.valueOf(rating).equals(Double.valueOf(RatingLevel.RATING_3.getValue()))) {
            return RatingLevel.RATING_3;
        } else if (Double.valueOf(rating).equals(Double.valueOf(RatingLevel.RATING_4.getValue()))) {
            return RatingLevel.RATING_4;
        } else if (Double.valueOf(rating).equals(Double.valueOf(RatingLevel.RATING_5.getValue()))) {
            return RatingLevel.RATING_5;
        }
        return RatingLevel.EMPTY;
    }

}
