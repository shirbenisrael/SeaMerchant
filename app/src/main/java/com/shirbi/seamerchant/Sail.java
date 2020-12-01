package com.shirbi.seamerchant;

public class Sail {
    State mDestination;
    State mSource;
    int mLandingHour;
    int mValueOnShip; // inventory + cash
    boolean mNightSail;
    boolean mBrokenShip;
    boolean mTooLoaded;
    int mGuardShipCost;
    int mMaxGuardShips;
    int mSelectedNumGuardShips;
    int mTotalGuardShipsCost;
    int mGuardShipCostPercent;
    Weather mSailWeather;
    static final int DEFAULT_GUARD_COST_PERCENT = 2;
    static final int MAX_GUARD_SHIPS = 5;
    static final int MIN_GUARD_SHIP_COST = 50;
    static final int DEFAULT_NUM_GUARDS = 0;

    public Sail(Logic logic, State destination) {
        mDestination = destination;
        mSource = logic.mCurrentState;
        mLandingHour = logic.mCurrentHour + logic.getSailDuration(mDestination);
        mValueOnShip = logic.mCash;

        int totalLoad = 0;
        for (Goods goods : Goods.values()) {
            mValueOnShip += logic.mInventory[goods.getValue()] * logic.mPriceTable.getPrice(mSource, goods);
            totalLoad += logic.mInventory[goods.getValue()];
        }

        mGuardShipCostPercent = DEFAULT_GUARD_COST_PERCENT;
        mGuardShipCost = Math.max(MIN_GUARD_SHIP_COST, (mGuardShipCostPercent * mValueOnShip / 100));
        mMaxGuardShips = Math.min(MAX_GUARD_SHIPS, logic.mCash / mGuardShipCost);

        mNightSail = (logic.getDayPart() != DayPart.SUN_SHINES);

        mSailWeather = Weather.GOOD_SAILING;
        switch (logic.mWeather) {
            case FOG:
                if (mDestination == logic.mWeatherState) {
                    mSailWeather = logic.mWeather;
                }
                break;
            case WIND:
            case STORM:
                if (mDestination == logic.mWeatherState || mSource == logic.mWeatherState) {
                    mSailWeather = logic.mWeather;
                }
                break;
            default:
                break;
        }

        mBrokenShip = (logic.mDamage != 0);
        mTooLoaded = (totalLoad > logic.mCapacity);

        selectNumGuardShips(DEFAULT_NUM_GUARDS);
    }

    public void selectNumGuardShips(int numGuards) {
        mSelectedNumGuardShips = numGuards;
        mTotalGuardShipsCost = mSelectedNumGuardShips * mGuardShipCost;
    }

    public boolean isPirateAppear() {
        return true;
    }
}
