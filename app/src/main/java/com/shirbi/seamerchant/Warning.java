package com.shirbi.seamerchant;

import androidx.annotation.StringRes;

public enum Warning {
    DAMAGED_SHIP(0),
    OVERLOAD(1),
    NIGHT_SAIL(2),
    WEATHER(3);

    public static final int NUM_WARNING = 3;

    private final int value;
    Warning(int value){
        this.value = value;
    }

    int[] mTitles = {
            R.string.DANGER_BROKEN_SHIP_TITLE,
            R.string.DANGER_OVERLOAD_TITLE,
            R.string.DANGER_NIGHT_SAIL_TITLE,
            R.string.DANGER_WEATHER_TITLE};

    public int getValue() {
        return value;
    }

    public static Warning first() {
        return values()[0];
    }

    public Warning next() {
        if (value + 1 == NUM_WARNING) {
            return null;
        }

        return values()[value + 1];
    }

    public @StringRes
    int toTitleStringId() {
        return mTitles[value];
    }
}
