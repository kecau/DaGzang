package com.universal.recommender.service;

import com.universal.recommender.cache.RatingStatisticCache;
import com.universal.recommender.enums.RatingLevel;
import com.universal.recommender.model.RatingStatisticEntity;
import com.universal.recommender.repository.StatisticRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

@Service
public class StatisticServiceImpl implements StatisticService {

    private static Long DEFAULT_RATING_COUNT = 0l;

    private final StatisticRepository statisticRepository;

    private final RatingStatisticCache ratingStatisticCache;

    public StatisticServiceImpl(StatisticRepository statisticRepository,
                                RatingStatisticCache ratingStatisticCache) {
        this.statisticRepository = statisticRepository;
        this.ratingStatisticCache = ratingStatisticCache;
    }

    @Override
    @Transactional
    public List<Long> getAllRatingStatistics() {
        ConcurrentMap<RatingLevel, Long> ratingLevelMap = ratingStatisticCache.getRatingStatisticMap();
        List<Long> ratingCount = new ArrayList<>();
        Set<RatingLevel> ratingLevels = ratingLevelMap.keySet();
        Stream.of(RatingLevel.values()).forEach(ratingLevel -> {
            ratingCount.add(getRatingCount(ratingLevelMap, ratingLevels, ratingLevel));
        });
        return ratingCount;
    }

    private Long getRatingCount(ConcurrentMap<RatingLevel, Long> ratingLevelMap, Set<RatingLevel> ratingLevels, RatingLevel ratingLevel) {
        if (ratingLevels.contains(ratingLevel)) {
            return ratingLevelMap.get(ratingLevel);
        } else {
            return DEFAULT_RATING_COUNT;
        }
    }

    @Override
    @Transactional
    public void saveAllRatingStatistic(List<RatingStatisticEntity> ratingStatisticEntities) {
        statisticRepository.saveAll(ratingStatisticEntities);
    }

    @Override
    public void saveAllRatingStatistic(ConcurrentMap<RatingLevel, Long> ratingStatistic) {
        ratingStatisticCache.getRatingStatisticMap().clear();
        ratingStatisticCache.setRatingStatisticMap(ratingStatistic);
    }

    @Override
    @Transactional
    public void deleteOldStatistic() {
        statisticRepository.deleteAllInBatch();
    }
}
