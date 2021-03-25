package com.universal.recommender.cache;

import com.universal.recommender.enums.RatingLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingStatisticCache {
    private ConcurrentMap<RatingLevel, Long> ratingStatisticMap = new ConcurrentHashMap<>();
}
