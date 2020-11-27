package com.shirbi.seamerchant;

import androidx.annotation.DrawableRes;

public enum DayPart {
    SUN_SHINES(0),
    EVENING(1),
    NIGHT(2);

    private static final @DrawableRes
    int[] mImageIds = {
            R.drawable.background_sunshines, R.drawable.background_evening, R.drawable.background_night};

    public static final int NUM_DAY_PART_TYPES = 3;

    private final int value;
    DayPart(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public @DrawableRes int toImageId() {
        return mImageIds[value];
    }


}
