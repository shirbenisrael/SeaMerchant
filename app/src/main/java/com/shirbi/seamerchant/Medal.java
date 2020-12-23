package com.shirbi.seamerchant;

public enum Medal {
    TREASURE_1(0), // 10,000
    TREASURE_2(1), // 100,000
    TREASURE_3(2), // 1,000,000
    TREASURE_4(3), // 2,000,000
    TREASURE_5(4), // 10,000,000
    IRON_BANK(5), // 5,000 interest
    CAPACITY_1(6), // 200
    CAPACITY_2(7), // 500
    CAPACITY_3(8), // 1000
    AROUND_THE_WORLD(9), // in one day
    ESCAPE(10), // 3 times in one day.
    CREW_NEGOTIATOR(11), // 2 times against strike
    WIND(12), // 3 times in a day of wrong navigation,
    YOUNG_FIGHTER(13), // First win pirates
    TIRED_FIGHTER(14), // 3 wins pirates in one day.
    ALWAYS_FIGHTER(15), // win 10 times. no escape or negotiate pirates.
    EGYPT_WHEAT(16), // 10,000 tonne of wheat double price in night in egypt.
    DOUBLE_SAIL(17), // double the ship value in one sail.
    TITANIC(18), // damage of 200,000 from shoal,
    ALL_GOODS(19), // pass all goods type and profit in one sail.
    BDS_TURKEY(20), // profit of 1,000,000 without enter turkey.
    GOOD_DAY(21), // x2 value in one day.
    GOOD_DAY_2(22), // x5 value in one day.
    GOOD_DAY_3(23), // x8 value in one day.
    GREECE_VISITOR(24), // visit greece all days and earn 1,000,000
    FEDERAL_RESERVE(25), // 90% value in bank in all 6 days + 1,000,000 profit.
    FAST_EXIT(26), // 1,000,000 in TUESDAY
    DIET_MERCHANT(27), // 1,000,000 without olives.
    HERO_DIE(28), // 2,000,000 damage when win pirates.
    GERMAN_TIME(29), // end all 7 days at 24:00 and profit of 1,000,000
    ECONOMICAL_SAIL(30); // profit of 2,000,000 and no sail which most value is cash.

    public static final int NUM_MEDAL_TYPES = 31;
    private final int value;
    Medal(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
