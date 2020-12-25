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
    GOOD_DAY_1(21), // x2 value in one day.
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

    public int getTitle() {
        switch (this) {
            case TREASURE_1:
                return R.string.TREASURE_1_TITLE;
            case TREASURE_2:
                return R.string.TREASURE_2_TITLE;
            case TREASURE_3:
                return R.string.TREASURE_3_TITLE;
            case TREASURE_4:
                return R.string.TREASURE_4_TITLE;
            case TREASURE_5:
                return R.string.TREASURE_5_TITLE;
            case IRON_BANK:
                return R.string.IRON_BANK_TITLE;
            case CAPACITY_1:
                return R.string.CAPACITY_1_TITLE;
            case CAPACITY_2:
                return R.string.CAPACITY_2_TITLE;
            case CAPACITY_3:
                return R.string.CAPACITY_3_TITLE;
            case AROUND_THE_WORLD:
                return R.string.AROUND_THE_WORLD_TITLE;
            case ESCAPE:
                return R.string.ESCAPE_TITLE;
            case CREW_NEGOTIATOR:
                return R.string.CREW_NEGOTIATOR_TITLE;
            case WIND:
                return R.string.WIND_TITLE;
            case YOUNG_FIGHTER:
                return R.string.YOUNG_FIGHTER_TITLE;
            case TIRED_FIGHTER:
                return R.string.TIRED_FIGHTER_TITLE;
            case ALWAYS_FIGHTER:
                return R.string.ALWAYS_FIGHTER_TITLE;
            case EGYPT_WHEAT:
                return R.string.EGYPT_WHEAT_TITLE;
            case DOUBLE_SAIL:
                return R.string.DOUBLE_SAIL_TITLE;
            case TITANIC:
                return R.string.TITANIC_TITLE;
            case ALL_GOODS:
                return R.string.ALL_GOODS_TITLE;
            case BDS_TURKEY:
                return R.string.BDS_TURKEY_TITLE;
            case GOOD_DAY_1:
                return R.string.GOOD_DAY_1_TITLE;
            case GOOD_DAY_2:
                return R.string.GOOD_DAY_2_TITLE;
            case GOOD_DAY_3:
                return R.string.GOOD_DAY_3_TITLE;
            case GREECE_VISITOR:
                return R.string.GREECE_VISITOR_TITLE;
            case FEDERAL_RESERVE:
                return R.string.FEDERAL_RESERVE_TITLE;
            case FAST_EXIT:
                return R.string.FAST_EXIT_TITLE;
            case DIET_MERCHANT:
                return R.string.DIET_MERCHANT_TITLE;
            case HERO_DIE:
                return R.string.HERO_DIE_TITLE;
            case GERMAN_TIME:
                return R.string.GERMAN_TIME_TITLE;
            case ECONOMICAL_SAIL:
                return R.string.ECONOMICAL_SAIL_TITLE;
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }

    public int getCondition() {
        switch (this) {
            case TREASURE_1:
                return R.string.TREASURE_1_CONDITION;
            case TREASURE_2:
                return R.string.TREASURE_2_CONDITION;
            case TREASURE_3:
                return R.string.TREASURE_3_CONDITION;
            case TREASURE_4:
                return R.string.TREASURE_4_CONDITION;
            case TREASURE_5:
                return R.string.TREASURE_5_CONDITION;
            case IRON_BANK:
                return R.string.IRON_BANK_CONDITION;
            case CAPACITY_1:
                return R.string.CAPACITY_1_CONDITION;
            case CAPACITY_2:
                return R.string.CAPACITY_2_CONDITION;
            case CAPACITY_3:
                return R.string.CAPACITY_3_CONDITION;
            case AROUND_THE_WORLD:
                return R.string.AROUND_THE_WORLD_CONDITION;
            case ESCAPE:
                return R.string.ESCAPE_CONDITION;
            case CREW_NEGOTIATOR:
                return R.string.CREW_NEGOTIATOR_CONDITION;
            case WIND:
                return R.string.WIND_CONDITION;
            case YOUNG_FIGHTER:
                return R.string.YOUNG_FIGHTER_CONDITION;
            case TIRED_FIGHTER:
                return R.string.TIRED_FIGHTER_CONDITION;
            case ALWAYS_FIGHTER:
                return R.string.ALWAYS_FIGHTER_CONDITION;
            case EGYPT_WHEAT:
                return R.string.EGYPT_WHEAT_CONDITION;
            case DOUBLE_SAIL:
                return R.string.DOUBLE_SAIL_CONDITION;
            case TITANIC:
                return R.string.TITANIC_CONDITION;
            case ALL_GOODS:
                return R.string.ALL_GOODS_CONDITION;
            case BDS_TURKEY:
                return R.string.BDS_TURKEY_CONDITION;
            case GOOD_DAY_1:
                return R.string.GOOD_DAY_1_CONDITION;
            case GOOD_DAY_2:
                return R.string.GOOD_DAY_2_CONDITION;
            case GOOD_DAY_3:
                return R.string.GOOD_DAY_3_CONDITION;
            case GREECE_VISITOR:
                return R.string.GREECE_VISITOR_CONDITION;
            case FEDERAL_RESERVE:
                return R.string.FEDERAL_RESERVE_CONDITION;
            case FAST_EXIT:
                return R.string.FAST_EXIT_CONDITION;
            case DIET_MERCHANT:
                return R.string.DIET_MERCHANT_CONDITION;
            case HERO_DIE:
                return R.string.HERO_DIE_CONDITION;
            case GERMAN_TIME:
                return R.string.GERMAN_TIME_CONDITION;
            case ECONOMICAL_SAIL:
                return R.string.ECONOMICAL_SAIL_CONDITION;
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }
}
