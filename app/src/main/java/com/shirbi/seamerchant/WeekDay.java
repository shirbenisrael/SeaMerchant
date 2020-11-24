package com.shirbi.seamerchant;

public enum WeekDay {
    SUNDAY(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6);

    public static final int NUM_WEEK_DAYS = 7;

    private static final int[] mStrings = {
            R.string.SUNDAY, R.string.MONDAY, R.string.TUESDAY, R.string.WEDNESDAY,
            R.string.THURSDAY, R.string.FRIDAY, R.string.SATURDAY};

    private final int value;
    WeekDay(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int toStringId() {
        return mStrings[value];
    }


}
