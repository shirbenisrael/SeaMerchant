package com.shirbi.seamerchant;

public enum Window {
    MAIN_WINDOW(0),
    MARKET_WINDOW(1),
    SAIL_WINDOW(2),
    SAIL_END_WINDOW(3),
    SLEEP_WINDOW(4);

    private static final int[] mLayoutId = {
            R.id.main_window_layout,
            R.id.market_layout,
            R.id.sail_layout,
            R.id.end_of_sail_layout,
            R.id.go_to_sleep_layout};

    private final int value;
    Window(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int toLayoutId() {
        return mLayoutId[value];
    }
}

