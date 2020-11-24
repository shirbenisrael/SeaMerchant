package com.shirbi.seamerchant;

public enum Weather {
    GOOD_SAILING(0),
    STORM(1),
    FOG(2);

    public static final int NUM_WEATHER_TYPES = 3;

    private final int value;
    Weather(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
