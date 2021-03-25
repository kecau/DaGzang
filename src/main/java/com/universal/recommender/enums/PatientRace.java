package com.universal.recommender.enums;

public enum  PatientRace {
    RACE1("race1", "1"),
    RACE2("race2", "2"),
    RACE3("race3", "3"),
    RACE4("race4", "4"),
    RACE5("race5", "5");

    String value;
    String rating;

    PatientRace(String value, String rating) {
        this.value = value;
        this.rating = rating;
    }

    public String getValue() {
        return value;
    }

    public String getRating() {
        return rating;
    }
}
