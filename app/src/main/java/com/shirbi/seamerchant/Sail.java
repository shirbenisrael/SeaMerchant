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
    public int mPiratesCapacity;
    public int mPiratesTreasure;
    public int mPiratesDamage;
    public boolean mIsPirateStoleGoods;
    public Goods mPiratesStolenGoods;
    public int mPiratesStolen;

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
        mPiratesDamage = 0;

        Random rand = new Random();

        if (!isWinPiratesSucceeds()) {
            mBattleResult = BattleResult.LOSE;
            mPiratesDamage = 100 + rand.nextInt(mLogic.mCapacity * mLogic.mCapacity - 99);
            mLogic.mDamage += mPiratesDamage;

            if (mTotalLoad == 0) {
                mIsPirateStoleGoods = false;
                mPiratesStolen = rand.nextInt(1 + mLogic.mCash / 2);
                mLogic.mCash -= mPiratesStolen;
            } else {
                mIsPirateStoleGoods = true;
                mPiratesStolenGoods = Goods.WHEAT;
                for (Goods goods : Goods.values()) {
                    if (mLogic.mInventory[goods.getValue()] >= mLogic.mInventory[mPiratesStolenGoods.getValue()]) {
                        mPiratesStolenGoods = goods;
                    }
                }
                mPiratesStolen = 1 + rand.nextInt(1 + mLogic.mInventory[mPiratesStolenGoods.getValue()] / 2);
                mLogic.mInventory[mPiratesStolenGoods.getValue()] -= mPiratesStolen;
            }

            return;
        }

        if (isWinCapturePirates()) {
            mBattleResult = BattleResult.WIN_AND_CAPTURE;
            mPiratesCapacity = rand.nextInt(mLogic.mCapacity / 25) * 25 + 25;
            mLogic.mCapacity += mPiratesCapacity;
        } else {
            mBattleResult = BattleResult.WIN_AND_TREASURE;
            mPiratesTreasure = 1 + rand.nextInt(mValueOnShip + mLogic.mBankDeposit) / 3;
            mLogic.mCash += mPiratesTreasure;
        }


        if (rand.nextInt(6) >= mSelectedNumGuardShips) {
            mPiratesDamage = 1 + rand.nextInt(mLogic.mCapacity * mLogic.mCapacity / 5);
            mLogic.mDamage += mPiratesDamage;
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
