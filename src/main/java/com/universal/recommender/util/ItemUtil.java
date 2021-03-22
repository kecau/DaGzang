package com.universal.recommender.util;

import com.imsweb.datagenerator.utils.Distribution;
import com.universal.recommender.constant.DataGeneratorConstant;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ItemUtil {

    public static String generateItemId() {
        Distribution<String> itemIdDistribution =
                Distribution.of(Thread.currentThread().getContextClassLoader().getResource("generation/frequencies/item.csv"));
        return itemIdDistribution.getValue();
    }

    public static String generateItemIdByRange(int range) {
        NumberFormat numberFormat = new DecimalFormat(DataGeneratorConstant.ID_CODE_FORMAT);
        Integer itemIndex = RandomUtil.randomByMaxRange(range);
        return DataGeneratorConstant.ITEM_PREFIX + numberFormat.format(itemIndex);
    }
}
