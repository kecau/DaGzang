package com.universal.recommender.custom.generation;

import com.imsweb.datagenerator.naaccr.NaaccrDataGeneratorOptions;
import com.imsweb.datagenerator.naaccr.NaaccrDataGeneratorRule;
import com.imsweb.datagenerator.utils.Distribution;

import java.util.List;
import java.util.Map;

public class RatingRule extends NaaccrDataGeneratorRule {

    public static final String ID = "rating";

    public RatingRule() {
        super("rating", "Rating");
    }

    @Override
    public void execute(Map<String, String> record, List<Map<String, String>> list, NaaccrDataGeneratorOptions naaccrDataGeneratorOptions, Map<String, Object> map1) {
        Distribution<String> ratingDistribution = Distribution.of(Thread.currentThread().getContextClassLoader().getResource("generation/frequencies/rating.csv"));
        record.put("rating", ratingDistribution.getValue());
    }
}
