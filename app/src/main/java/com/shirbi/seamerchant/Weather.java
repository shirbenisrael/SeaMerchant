package com.shirbi.seamerchant;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public enum Weather {
    GOOD_SAILING(0),
    STORM(1),
    FOG(2),
    WIND(3);

    public static final int NUM_WEATHER_TYPES = 4;

    private static final @StringRes
    int[] mStrings = {
            R.string.GOOD_SEA_STRING, R.string.STORM_STRING, R.string.FOG_STRING, R.string.WIND_STRING};

    private static final @DrawableRes
    int[] mBackGrounds = {
            R.drawable.good_sea_weather, R.drawable.storm_weather, R.drawable.fog_weather, R.drawable.wind_weather};

    private static final @DrawableRes
    int[] mSmallIcon = {
            R.drawable.good_sea_weather, R.drawable.storm_weather, R.drawable.fog_weather, R.drawable.wind_icon};

    private final int value;
    Weather(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public @StringRes int toStringId() {
        return mStrings[value];
    }

    public @DrawableRes int toBackground() {
        return mBackGrounds[value];
    }

    public @DrawableRes int toSmallIcon() {
        return mSmallIcon[value];
    }
}
