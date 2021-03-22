package com.universal.recommender.util;

import com.imsweb.datagenerator.utils.Distribution;
import com.universal.recommender.constant.DataGeneratorConstant;

public class RatingUtil {

    public static String generateRating() {
        Distribution<String> ratingDistribution =
                Distribution.of(Thread.currentThread().getContextClassLoader().getResource("generation/frequencies/rating.csv"));
        return ratingDistribution.getValue();
    }

    public static String generateRatingWithNullValue() {
        if (RandomUtil.isInPercent(DataGeneratorConstant.NULL_RATING_PERCENT)) {
            return generateRating();
        }
        return "";
    }

    public static String generateRating(String filePath) {
        Distribution<String> ratingDistribution =
                Distribution.of(Thread.currentThread().getContextClassLoader().getResource(filePath));
        return ratingDistribution.getValue();
    }

    public static String generateRatingWithNullValue(String filePath, Double ratingSparsity) {
        if (RandomUtil.isInPercent(ratingSparsity)) {
            return generateRating(filePath);
        }
        return "";
    }
}
