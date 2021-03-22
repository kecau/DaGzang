package com.universal.recommender.enums;

public enum RatingLevel {
    EMPTY(""),
    RATING_1("1"),
    RATING_2("2"),
    RATING_3("3"),
    RATING_4("4"),
    RATING_5("5");

    private String value;

    RatingLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
