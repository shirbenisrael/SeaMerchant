package com.shirbi.seamerchant;

public enum Window {
    MAIN_WINDOW(0),
    MARKET_WINDOW(1),
    SAIL_WINDOW(2),
    SAIL_END_WINDOW(3),
    SLEEP_WINDOW(4),
    WEATHER_WINDOW(5),
    PIRATES_WINDOW(6),
    ESCAPE_WINDOW(7),
    PIRATES_ATTACK_WINDOW(8),
    PIRATE_NEGOTIATE_WINDOW(9),
    ABANDONED_SHIP_WINDOW(10),
    BANK_WINDOW(11),
    BAD_WEATHER_IN_SAIL_WINDOW(12);

    private static final int[] mLayoutId = {
            R.id.main_window_layout,
            R.id.market_layout,
            R.id.sail_layout,
            R.id.end_of_sail_layout,
            R.id.go_to_sleep_layout,
            R.id.weather_layout,
            R.id.pirates_layout,
            R.id.escape_layout,
            R.id.attack_pirates_layout,
            R.id.negotiate_layout,
            R.id.abandoned_ship_layout,
            R.id.bank_layout,
            R.id.bad_weather_in_sail_layout};

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

