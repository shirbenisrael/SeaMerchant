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
    NEGOTIATE_WINDOW(9),
    PIRATE_ACCEPT_OFFER_WINDOW(10),
    ABANDONED_SHIP_WINDOW(11),
    BANK_WINDOW(12),
    BAD_WEATHER_IN_SAIL_WINDOW(13),
    SIMPLE_NEW_DAY_EVENT_WINDOW(14),
    STRIKE_WINDOW(15),
    FIX_SHIP_WINDOW(16),
    SHOAL_WINDOW(17),
    SINK_WINDOW(18),
    DANGER_WINDOW(19),
    HIGH_SCORE_WINDOW(20),
    MENU_WINDOW(21),
    MEDAL_WINDOW(22),
    NEW_MEDAL_WINDOW(23),
    ABOUT_WINDOW(24),
    OPEN_WINDOW(25),
    HELP_WINDOW(26);

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
            R.id.offer_accept_layout,
            R.id.abandoned_ship_layout,
            R.id.bank_layout,
            R.id.bad_weather_in_sail_layout,
            R.id.simple_new_day_event_layout,
            R.id.strike_layout,
            R.id.fix_ship_layout,
            R.id.shoal_layout,
            R.id.sink_layout,
            R.id.danger_layout,
            R.id.high_score_layout,
            R.id.menu_layout,
            R.id.medal_layout,
            R.id.new_medal_layout,
            R.id.about_layout,
            R.id.open_screen_layout,
            R.id.help_layout};

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

