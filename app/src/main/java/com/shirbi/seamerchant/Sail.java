package com.shirbi.seamerchant;

import java.util.Random;

public class Sail {
    private Random mRand = new Random();
    Logic mLogic;
    State mDestination;
    State mSource;
    int mLandingHour;
    long mValueOnShip; // inventory + cash
    boolean mNightSail;
    boolean mBrokenShip;
    long mTotalLoad;
    boolean mTooLoaded;
    long mGuardShipCost;
    int mMaxGuardShips;
    int mSelectedNumGuardShips;
    long mTotalGuardShipsCost;
    float mGuardShipCostPercent;
    Weather mSailWeather;
    boolean mSailEndedPeacefully;
    public Warning mWarning;
    private int mPiratesCounter;
    private boolean mIsFogInSail;

    static final float DEFAULT_GUARD_COST_PERCENT = 2f;
    public static final float NIGHT_SAIL_GUARD_COST_PERCENT_MULTIPLY = 1.25f;
    static final float STORM_GUARD_COST_PERCENT_MULTIPLY = 2f;
    static final float FOG_GUARD_COST_PERCENT_MULTIPLY = 1.5f;
    static final float WIND_GUARD_COST_PERCENT_MULTIPLY = 1.5f;
    static final int MAX_GUARD_SHIPS = 5;
    static final int MIN_GUARD_SHIP_COST = 50;
    static final int DEFAULT_NUM_GUARDS = 0;
    static final int[] mChancesToWinPirates = {1, 51, 76, 88, 94, 97};
    static final int PERCENT_OF_WRONG_NAVIGATION_ON_WIND = 33;
    static final int PERCENT_OF_STORM_APPEAR = 33;
    static final int PERCENT_OF_FOG_APPEAR = 50;
    static final int PERCENT_OF_ABANDONED_SHIP_APPEAR = 11;
    static final int PERCENT_OF_SHOAL_APPEAR = 33;
    static final int PERCENT_OF_PIRATES_APPEAR = 33;

    public enum BattleResult {
        WIN_AND_CAPTURE,
        WIN_AND_TREASURE,
        LOSE
    }

    public BattleResult mBattleResult;
    public long mPiratesCapacity;
    public long mPiratesTreasure;
    public long mPiratesDamage;
    public boolean mIsPirateStoleGoods;
    public Goods mPiratesStolenGoods;
    public long mPiratesStolen;

    public boolean mIsStormWashGoods; // Else ship damage
    public Goods mStormLostGoodType;
    public long mStormLostUnits;

    public Goods mAbandonedShipGoods;
    public long mAbandonedShipGoodsUnits;

    public long mShoalDamage;

    public Goods mSinkGood;
    public long mSinkGoodsUnitsLost;

    private void calculateShipValue() {
        mValueOnShip = mLogic.mCash;
        mTotalLoad = 0;
        for (Goods goods : Goods.values()) {
            mValueOnShip += mLogic.mInventory[goods.getValue()] * mLogic.mPriceTable.getPrice(mSource, goods);
            mTotalLoad += mLogic.mInventory[goods.getValue()];
        }
    }

    public Sail(Logic logic, State destination) {
        mLogic = logic;
        mDestination = destination;
        mSource = logic.mCurrentState;
        mLandingHour = logic.mCurrentHour + logic.getSailDuration(mDestination);
        calculateShipValue();

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

        mGuardShipCostPercent = DEFAULT_GUARD_COST_PERCENT;
        if (mNightSail) {
            mGuardShipCostPercent *= NIGHT_SAIL_GUARD_COST_PERCENT_MULTIPLY;
        }

        switch (mSailWeather) {
            case STORM:
                mGuardShipCostPercent *= STORM_GUARD_COST_PERCENT_MULTIPLY;
                break;
            case WIND:
                mGuardShipCostPercent *= WIND_GUARD_COST_PERCENT_MULTIPLY;
                break;
            case FOG:
                mGuardShipCostPercent *= FOG_GUARD_COST_PERCENT_MULTIPLY;
                break;
            default:
                break;
        }

        mGuardShipCost = (long)Math.max(MIN_GUARD_SHIP_COST, (mGuardShipCostPercent * mValueOnShip / 100));
        mMaxGuardShips = (int)Math.min(MAX_GUARD_SHIPS, ((logic.mCash / mGuardShipCost) + (mLogic.hasMedal(Medal.ALWAYS_FIGHTER) ? 1 : 0)));

        mBrokenShip = (logic.mDamage != 0);
        mTooLoaded = (mTotalLoad > logic.mCapacity);

        selectNumGuardShips(DEFAULT_NUM_GUARDS);

        mSailEndedPeacefully = true;

        mWarning = Warning.first();

        mPiratesCounter = 0;
        mIsFogInSail = false;
    }

    public void selectNumGuardShips(int numGuards) {
        mSelectedNumGuardShips = numGuards;
        mTotalGuardShipsCost = mSelectedNumGuardShips * mGuardShipCost;
        if ((mLogic.hasMedal(Medal.ALWAYS_FIGHTER)) && (mSelectedNumGuardShips > 0)) {
            mTotalGuardShipsCost -= mGuardShipCost;
        }
    }

    public boolean isAbandonedShipAppear() {
        return tryToDoSomething(PERCENT_OF_ABANDONED_SHIP_APPEAR);
    }

    public boolean isShoalInSail() {
        if (mLogic.mCurrentHour >= mLogic.getEveningTime()) {
            return tryToDoSomething(PERCENT_OF_SHOAL_APPEAR);
        }

        return false;
    }

    public boolean isSinkInSail() {
        long load = mLogic.calculateLoad();
        if (load <= mLogic.mCapacity) {
            return false;
        }
        return (mLogic.generateRandom(load) >= mLogic.mCapacity);
    }

    public boolean isBadWeatherInSail() {
        if ((mSource == mLogic.mWeatherState) || (mDestination == mLogic.mWeatherState)) {
            if (mLogic.mWeather == Weather.WIND) {
                boolean wrongNavigation = tryToDoSomething(PERCENT_OF_WRONG_NAVIGATION_ON_WIND);
                if (wrongNavigation) {
                    mLogic.mWrongNavigationCountInOneDay++;
                }
                return wrongNavigation;
            } else if (mLogic.mWeather == Weather.STORM) {
                return tryToDoSomething(PERCENT_OF_STORM_APPEAR);
            }
        }
        return false;
    }

    public boolean isFogInSail() {
        if ((mDestination == mLogic.mWeatherState)) {
            if (mLandingHour + mLogic.getSailDuration(mSource, mDestination) >= Logic.SLEEP_TIME) {
                return false;
            }

            if (mLogic.mWeather == Weather.FOG) {
                boolean isFogAppear =  tryToDoSomething(PERCENT_OF_FOG_APPEAR);
                mIsFogInSail |= isFogAppear;
                return isFogAppear;
            }
        }
        return false;
    }

    public void swapSourceAndDestination() {
        State temp = mSource;
        mSource = mDestination;
        mDestination = temp;
    }

    public void setNewDestinationAfterFog(State state) {
        mDestination = state;
    }

    public void createBadWeatherInSail() {
        mSailEndedPeacefully = false;
        if (mLogic.mWeather == Weather.WIND || mLogic.mWeather == Weather.FOG) {
            swapSourceAndDestination();
        } else if (mLogic.mWeather == Weather.STORM) {
            if (mTotalLoad == 0) {
                mIsStormWashGoods = false; // damage to ship
                // Easy start - small damage from storm before achieve 100,000
                if (mLogic.hasMedal(Medal.TREASURE_2)) {
                    mStormLostUnits = 100 + mLogic.generateRandom(mLogic.mCapacity * mLogic.mCapacity);
                } else {
                    mStormLostUnits = 100 + mLogic.generateRandom(1000);
                }
                mLogic.mDamage += mStormLostUnits;
            } else {
                mIsStormWashGoods = true;
                mStormLostGoodType = Goods.WHEAT;
                for (Goods goods : Goods.values()) {
                    if (mLogic.mInventory[goods.getValue()] >= mLogic.mInventory[mStormLostGoodType.getValue()]) {
                        mStormLostGoodType = goods;
                    }
                }
                mStormLostUnits = 1 + mLogic.generateRandom(1 + mLogic.mInventory[mStormLostGoodType.getValue()] / 2);
                mLogic.mInventory[mStormLostGoodType.getValue()] -= mStormLostUnits;
            }
        }
    }

    public void createAbandonedShip() {
        mAbandonedShipGoods = Goods.values()[mRand.nextInt(Goods.NUM_GOODS_TYPES)];
        mAbandonedShipGoodsUnits = mLogic.generateRandom(mLogic.mBankDeposit + mValueOnShip + 1) /
                mLogic.mPriceTable.getPrice(mSource, mAbandonedShipGoods) + 1;
        mLogic.addGoodsToInventory(mAbandonedShipGoods, mAbandonedShipGoodsUnits);
    }

    public long calculateMaxShoalDamage() {
        return mLogic.mCapacity * mLogic.mCapacity;
    }

    private long generateShoalDamage() {
        // Easy start - small damage from shoal before achieve 100,000
        if (mLogic.hasMedal(Medal.TREASURE_2)) {
            return 100 + mLogic.generateRandom(mLogic.mCapacity * mLogic.mCapacity - 99);
        } else {
            return 100 + mLogic.generateRandom(1000);
        }
    }

    public void createShoal() {
        mSailEndedPeacefully = false;
        mShoalDamage = generateShoalDamage();
        if (mLogic.hasMedal(Medal.TITANIC)) {
            mShoalDamage = Math.min(mShoalDamage, generateShoalDamage());
        }

        mLogic.mDamage += mShoalDamage;
        if (mShoalDamage >= 200000) {
            mLogic.mTitanicMedal = true;
        }
    }

    public void createSink() {
        mSailEndedPeacefully = false;
        mSinkGood = mLogic.findGoodsWithMostUnits();
        mSinkGoodsUnitsLost = 1 + mLogic.generateRandom(mLogic.mInventory[mSinkGood.getValue()]);
        mLogic.removeGoodsFromInventory(mSinkGood, mSinkGoodsUnitsLost);
    }

    public boolean isPirateAppear() {
        int chances = PERCENT_OF_PIRATES_APPEAR;
        if (mDestination == State.GREECE || mSource == State.GREECE) {
            if (mLogic.hasMedal(Medal.TIRED_FIGHTER)) {
                chances *= 2;
            }
        }

        boolean isPirateAppear = tryToDoSomething(chances);

        mPiratesCounter += isPirateAppear ? 1 : 0;
        if (mPiratesCounter == 2 && mIsFogInSail) {
            mLogic.mFogOfWarMedal = true;
        }
        return isPirateAppear;
    }

    public void calculateBattleResult() {
        mPiratesDamage = 0;

        if (!isWinPiratesSucceeds()) {
            mSailEndedPeacefully = false;
            mBattleResult = BattleResult.LOSE;

            // Easy start - small damage from pirates before achieve 100,000
            if (mLogic.hasMedal(Medal.TREASURE_2)) {
                mPiratesDamage = 100 + mLogic.generateRandom(mLogic.mCapacity * mLogic.mCapacity - 99);
            } else {
                mPiratesDamage = 100 + mLogic.generateRandom(1000);
            }
            mLogic.mDamage += mPiratesDamage;
            mLogic.disableWinPiratesCount();

            if (mTotalLoad == 0) {
                mIsPirateStoleGoods = false;
                mPiratesStolen = mLogic.generateRandom(1 + mLogic.mCash / 2);
                mLogic.mCash -= mPiratesStolen;
            } else {
                mIsPirateStoleGoods = true;
                mPiratesStolenGoods = Goods.WHEAT;
                for (Goods goods : Goods.values()) {
                    if (mLogic.mInventory[goods.getValue()] >= mLogic.mInventory[mPiratesStolenGoods.getValue()]) {
                        mPiratesStolenGoods = goods;
                    }
                }
                mPiratesStolen = 1 + mLogic.generateRandom(1 + mLogic.mInventory[mPiratesStolenGoods.getValue()] / 2);
                mLogic.removeGoodsFromInventory(mPiratesStolenGoods, mPiratesStolen);
            }

            return;
        }

        mLogic.mWinPiratesCountInOneDay++;
        mLogic.mWinPiratesCount++;

        if (isWinCapturePirates()) {
            mBattleResult = BattleResult.WIN_AND_CAPTURE;
            mPiratesCapacity = mLogic.generateRandom(mLogic.mCapacity / 25) * 25 + 25;
            mLogic.mCapacity += mPiratesCapacity;
        } else {
            mBattleResult = BattleResult.WIN_AND_TREASURE;
            mPiratesTreasure = 1 + mLogic.generateRandom(mValueOnShip + mLogic.mBankDeposit) / 3;
            mLogic.mCash += mPiratesTreasure;
        }

        if (mRand.nextInt(6) >= mSelectedNumGuardShips) {
            mPiratesDamage = generateWinPirateDamage();
            if (mLogic.hasMedal(Medal.HERO_DIE)) {
                mPiratesDamage = Math.min(mPiratesDamage, generateWinPirateDamage());
            }
            mLogic.mDamage += mPiratesDamage;
            mSailEndedPeacefully = false;

            if (mPiratesDamage >= 2000000) {
                mLogic.mHeroDieMedal = true;
            }
        }
    }

    private long generateWinPirateDamage() {
        return 1 + mLogic.generateRandom(mLogic.mCapacity * mLogic.mCapacity / 5);
    }

    public boolean isEscapePiratesSucceeds() {
        boolean result = tryToDoSomething(getPercentsToEscapeFromPirates());

        if (result) {
            mLogic.mEscapeCountInOneDay++;
            mLogic.disableWinPiratesCount();
        }

        return result;
    }

    public void negotiationSucceeds() {
        calculateShipValue();
        mSailEndedPeacefully = false;
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

        return (int)(100 * (mLogic.mCapacity - mTotalLoad) / mLogic.mCapacity);
    }

    private boolean tryToDoSomething(int percentsToSucceed) {
        int result = mRand.nextInt(100);

        return result < percentsToSucceed;
    }

    public boolean isWarningRelevant() {
        switch (mWarning) {
            case DAMAGED_SHIP:
                return mLogic.mDamage > 0;
            case OVERLOAD:
                return mLogic.calculateLoad() > mLogic.mCapacity;
            case NIGHT_SAIL:
                return (mLogic.getDayPart() != DayPart.SUN_SHINES);
            case WEATHER:
                if (mLogic.mWeather != Weather.GOOD_SAILING) {
                    if ((mLogic.mWeather == Weather.FOG) && (mLogic.mWeatherState == mDestination)) {
                        return true;
                    }
                    if ((mLogic.mWeather != Weather.FOG) &&
                        ((mLogic.mWeatherState == mDestination) || (mLogic.mWeatherState == mSource))) {
                        return true;
                    }
                }
                return  false;
        }
        return false;
    }

    public void setNextWarning() {
        mWarning = mWarning.next();
    }

    static float getWeatherGuardCostMultiplyFromHere(Weather weather) {
        switch (weather) {
            case GOOD_SAILING:
            case FOG:
                return 1.0f;
            case STORM:
                return STORM_GUARD_COST_PERCENT_MULTIPLY;
            case WIND:
                return WIND_GUARD_COST_PERCENT_MULTIPLY;
        }

        return 1.0f;
    }

    static float getWeatherGuardCostMultiplyToThere(Weather weather) {
        switch (weather) {
            case GOOD_SAILING:
                return 1.0f;
            case FOG:
                return FOG_GUARD_COST_PERCENT_MULTIPLY;
            case STORM:
                return STORM_GUARD_COST_PERCENT_MULTIPLY;
            case WIND:
                return WIND_GUARD_COST_PERCENT_MULTIPLY;
        }

        return 1.0f;
    }
}
