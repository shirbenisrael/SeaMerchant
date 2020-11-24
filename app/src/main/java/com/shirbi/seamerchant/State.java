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
}
