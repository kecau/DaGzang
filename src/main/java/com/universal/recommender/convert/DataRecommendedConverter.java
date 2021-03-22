package com.universal.recommender.convert;

import com.universal.recommender.dto.RecommendedDataDto;
import net.librec.recommender.item.RecommendedItem;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DataRecommendedConverter {

    public static ConcurrentMap<UUID, RecommendedDataDto> convertFromRecommendedItemList(List<RecommendedItem> recommendedItemList) {
        if (CollectionUtils.isEmpty(recommendedItemList)) {
            return new ConcurrentHashMap<>();
        }
        ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap = new ConcurrentHashMap<>();
        recommendedItemList.stream().forEach(recommendedItem -> {
            recommendedDataMap.putAll(convertFromRecommendedItem(recommendedItem));
        });
        return recommendedDataMap;
    }

    private static ConcurrentMap<UUID, RecommendedDataDto> convertFromRecommendedItem(RecommendedItem recommendedItem) {
        ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap = new ConcurrentHashMap<>();
        UUID id = UUID.randomUUID();
        recommendedDataMap.put(id, RecommendedDataDto.builder()
        .id(id)
        .itemId(recommendedItem.getUserId())
        .userId(recommendedItem.getItemId())
        .rating(String.valueOf(recommendedItem.getValue()))
        .build());
        return recommendedDataMap;
    }
}
