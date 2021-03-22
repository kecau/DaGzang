package com.universal.recommender.util;

import com.fasterxml.jackson.databind.node.NumericNode;
import com.imsweb.datagenerator.utils.Distribution;
import com.universal.recommender.constant.DataGeneratorConstant;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class UserUtil {

    public static String generateUserId() {
        Distribution<String> userIdDistribution =
                Distribution.of(Thread.currentThread().getContextClassLoader().getResource("generation/frequencies/user.csv"));
        return userIdDistribution.getValue();
    }

    public static String generateUserId(int count) {
        NumberFormat numberFormat = new DecimalFormat(DataGeneratorConstant.ID_CODE_FORMAT);
        return DataGeneratorConstant.USER_PREFIX + numberFormat.format(count);
    }
}
