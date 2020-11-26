package com.shirbi.seamerchant;

public enum State {
    EGYPT(0),
    ISRAEL(1),
    TURKEY(2),
    CYPRUS(3),
    GREECE(4);

    public static final int NUM_STATES = 5;

    private static final int[] mStrings = {
            R.string.EGYPT, R.string.ISRAEL, R.string.TURKEY, R.string.CYPRUS, R.string.GREECE};

    private static final int[] mFlags = {
            R.drawable.flag_egypt, R.drawable.flag_israel, R.drawable.flag_turkey,
            R.drawable.flag_cyprus, R.drawable.flag_greece};

    public static final float[] mLocationsX = {0.55f, 0.90f, 0.87f, 0.63f, 0.07f};
    public static final float[] mLocationsY = {0.75f, 0.53f, 0.03f, 0.27f, 0.05f};

    public static final float[][] middleLocationX = {
            {0.00f, 0.78f, 0.55f, 0.57f, 0.00f}, // from egypt
            {0.78f, 0.00f, 0.90f, 0.73f, 0.00f}, // from israel
            {0.55f, 0.90f, 0.00f, 0.67f, 0.00f}, // from turkey
            {0.57f, 0.73f, 0.67f, 0.00f, 0.30f}, // from cyprus
            {0.00f, 0.00f, 0.00f, 0.30f, 0.00f}}; // from greece

    public static final float[][] middleLocationY = {
            {0.00f, 0.76f, 0.20f, 0.53f, 0.00f}, // from egypt
            {0.76f, 0.00f, 0.30f, 0.53f, 0.00f}, // from israel
            {0.20f, 0.30f, 0.00f, 0.20f, 0.00f}, // from turkey
            {0.53f, 0.53f, 0.20f, 0.00f, 0.27f}, // from cyprus
            {0.00f, 0.00f, 0.00f, 0.27f, 0.00f}}; // from greece

    private final int value;
    State(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int toStringId() {
        return mStrings[value];
    }

    public int toFlagId() {
        return mFlags[value];
    }

    public float toLocationX() {
        return mLocationsX[value];
    }

    public float toLocationY() {
        return mLocationsY[value];
    }

    public float toMiddlePointX(State otherState) {
        return middleLocationX[value][otherState.value];
    }

    public float toMiddlePointY(State otherState) {
        return middleLocationY[value][otherState.value];
    }
}
