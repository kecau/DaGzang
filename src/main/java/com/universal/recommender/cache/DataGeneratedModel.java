package com.universal.recommender.cache;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataGeneratedModel implements Comparable {

    private String userId;

    private String itemId;

    private String rating;

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
