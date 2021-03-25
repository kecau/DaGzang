package com.universal.recommender.util;

import com.universal.recommender.enums.SVDAlgorithm;

import java.util.Random;

import static com.universal.recommender.constant.DataGeneratorConstant.*;

public class RandomUtil {

    public static Integer randomTumorNumber() {
        return new Random().ints(1, 1, 3).findFirst().getAsInt();
    }

    public static boolean isInPercent(double percent) {
        Random random = new Random();
        int randomValue = random.nextInt(100);
        return (randomValue * 1.0)  < percent;
    }

    public static Integer randomByMaxRange(int maxRange) {
        Random random = new Random();
        return random.nextInt(maxRange);
    }

    public static Integer generateSdvValue(SVDAlgorithm svdAlgorithm) {
        Random r = new Random();
        if (SVDAlgorithm.SVD == svdAlgorithm) {
            return r.nextInt((SVD_MAX_VALUE - SVD_MIN_VALUE) + 1) + SVD_MIN_VALUE;
        } else {
            return r.nextInt((ISVD_MAX_VALUE - ISVD_MIN_VALUE) + 1) + ISVD_MIN_VALUE;
        }
    }

    public static Integer generateSdvValue(Integer minRange, Integer maxRange) {
        Random r = new Random();
        return r.nextInt((maxRange - minRange) + 1) + minRange;
    }
}
