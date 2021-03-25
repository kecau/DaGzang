package com.universal.recommender.constant;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataGeneratorConstant {

    public static final String ITEM_ID = "histologicTypeIcdO3";

    public static final String USER_ID = "patientIdNumber";

    public static final double NULL_RATING_PERCENT = 50;

    public static final String USER_PREFIX = "U";

    public static final String ITEM_PREFIX = "I";

    public static final String ID_CODE_FORMAT = "000000000";

    public static final String RATING_FILE_NAME = "generation/frequencies/rating.csv";

    public static final String DEFAULT_DATASET_FILE_NAME = "";

    public static final Integer DEFAULT_NUMBER_OF_COMMON_USER = 0;

    public static final Integer SVD_MIN_VALUE = 65;

    public static final Integer SVD_MAX_VALUE = 102;

    public static final Integer ISVD_MIN_VALUE = 60;

    public static final Integer ISVD_MAX_VALUE = 97;

    public static final Integer SVD_RANGE = 5;

    public static final Double PARAM_CONFIG_SPARSITY = 99.0;

    public static final Integer PARAM_CONFIG_RATING_SCALE = 5;

    public static final List<Integer> PARAM_CONFIG_USER_AMOUNT_LIST = Arrays.asList(10000, 20000, 30000, 40000, 50000);

    public static final List<Integer> PARAM_CONFIG_ITEM_AMOUNT_LIST = Arrays.asList(20000, 30000);

    public static final List<Integer> PARAM_CONFIG_COMMON_USER_AMOUNT_LIST = Arrays.asList(10, 20, 30);

    public static final Map<String, String> PARAM_CONFIG_DISTRIBUTION_FILE_PATH_MAP =
            Stream.of(
                    new AbstractMap.SimpleEntry<>("Poisson distribution", "generation/frequencies/rating_linear_rasing.csv"),
                    new AbstractMap.SimpleEntry<>("Pascal distribution", "generation/frequencies/rating_linear_downing.csv"),
                    new AbstractMap.SimpleEntry<>("Normal distribution", "generation/frequencies/rating_medium_highest.csv"))
             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
}
