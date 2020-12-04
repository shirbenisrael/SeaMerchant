package com.shirbi.seamerchant;

import java.util.Random;

public class Sail {
    Logic mLogic;
    State mDestination;
    State mSource;
    int mLandingHour;
    int mValueOnShip; // inventory + cash
    boolean mNightSail;
    boolean mBrokenShip;
    int mTotalLoad;
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
    static final int[] mChancesToWinPirates = {1, 51, 76, 88, 94, 97};

    public enum BattleResult {
        WIN_AND_CAPTURE,
        WIN_AND_TREASURE,
        LOSE
    }

    public BattleResult mBattleResult;

    public Sail(Logic logic, State destination) {
        mLogic = logic;
        mDestination = destination;
        mSource = logic.mCurrentState;
        mLandingHour = logic.mCurrentHour + logic.getSailDuration(mDestination);
        mValueOnShip = logic.mCash;
        mTotalLoad = 0;
        for (Goods goods : Goods.values()) {
            mValueOnShip += logic.mInventory[goods.getValue()] * logic.mPriceTable.getPrice(mSource, goods);
            mTotalLoad += logic.mInventory[goods.getValue()];
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
        mTooLoaded = (mTotalLoad > logic.mCapacity);

        selectNumGuardShips(DEFAULT_NUM_GUARDS);
    }

    public void selectNumGuardShips(int numGuards) {
        mSelectedNumGuardShips = numGuards;
        mTotalGuardShipsCost = mSelectedNumGuardShips * mGuardShipCost;
    }

    public boolean isPirateAppear() {
        return true;
    }

    public void calculateBattleResult() {
        if (!isWinPiratesSucceeds()) {
            mBattleResult = BattleResult.LOSE;
            return;
        }

        if (isWinCapturePirates()) {
            mBattleResult = BattleResult.WIN_AND_CAPTURE;
        } else {
            mBattleResult = BattleResult.WIN_AND_TREASURE;
        }
    }

    public boolean isEscapePiratesSucceeds() {
        return tryToDoSomething(getPercentsToEscapeFromPirates());
    }

    public boolean isWinPiratesSucceeds() {
        return tryToDoSomething(getPercentsToWinPirates());
    }

    public boolean isWinCapturePirates() {
        return tryToDoSomething(50);
    }

    public int getPercentsToWinPirates() {
        return mChancesToWinPirates[mSelectedNumGuardShips];
    }

    public int getPercentsToEscapeFromPirates() {
        if (mTotalLoad >= mLogic.mCapacity) {
            return 1;
        }

        return 100 * (mLogic.mCapacity - mTotalLoad) / mLogic.mCapacity;
    }

    private boolean tryToDoSomething(int percentsToSucceed) {
        Random rand = new Random();
        int result = rand.nextInt(100);

        return result < percentsToSucceed;
    }
}
