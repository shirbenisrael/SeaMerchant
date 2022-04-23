package com.shirbi.seamerchant;

import android.content.SharedPreferences;

import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class Logic {
    protected static final int START_GAME_CASH = 5000;
    private static final int START_GAME_CASH_WITH_MEDAL = 10000;
    public static final int START_HOUR = 6;
    public static final int START_DAMAGE = 0;
    private static final Weather START_WEATHER = Weather.GOOD_SAILING;
    private static final State START_STATE = State.ISRAEL;
    private static final State START_WEATHER_STATE = State.ISRAEL;
    protected static final int START_CAPACITY = 100;
    private static final int START_CAPACITY_WITH_MEDAL = 200;
    public static final int EVENING_TIME = 17;
    public static final int NIGHT_TIME = 21;
    public static final int SLEEP_TIME = 24;
    private static final int MIN_VALUE_FOR_MERCHANT = 4500;
    public static final int BANK_NIGHTLY_INTEREST = 10;
    public static final int BANK_NIGHTLY_INTEREST_WITH_MEDAL = 15;
    private static final int DEFAULT_NUM_GUARDS = Sail.MAX_GUARD_SHIPS;

    private MainActivity mActivity;

    public Logic(MainActivity activity) {
        mActivity = activity;

        for (int scoreType = 0; scoreType < Logic.ScoreType.SCORE_TYPES; scoreType++) {
            for (int i = 0; i < mScoreTable[scoreType].length; i++) {
                mScoreTable[scoreType][i] = new ScoreTable();
                mScoreTable[scoreType][i].rank = i + 1;
                mScoreTable[scoreType][i].name = "Shir";
                mScoreTable[scoreType][i].score = 100 - (i * i);
            }
        }
    }

    private int mSailDurations[][] = {
            { 0,  3,  6,  3, -1}, // From Egypt
            { 3,  0,  3,  3, -1}, // From Israel
            { 6,  3,  0,  3, -1}, // From Turkey
            { 3,  3,  3,  0,  6}, // From Cyprus
            {-1, -1, -1,  6,  0}  // From Greece
    };

    private void updateSailDuration(State state1, State state2) {
        mSailDurations[state1.getValue()][state2.getValue()] = 5;
        mSailDurations[state2.getValue()][state1.getValue()] = 5;
    }

    private Random mRand = new Random();

    // Variable need to store
    public PriceTable mPriceTable = new PriceTable(this);
    public long mInventory[] = new long[Goods.NUM_GOODS_TYPES];
    public long mCash;
    public long mBankDeposit;
    public WeekDay mCurrentDay;
    public int mCurrentHour;
    public State mCurrentState;
    public State mWeatherState;
    public Weather mWeather;
    public long mDamage;
    public long mCapacity;
    public boolean mIsBankOperationTakesTime;
    public boolean mIsMarketOperationTakesTime;
    public boolean mIsFixOperationTakesTime;
    public boolean mIsFreeFixUsed;
    private boolean mIsMedalAchieved[] = new boolean[Medal.values().length];
    private boolean mStatesVisitedToday[] = new boolean[State.values().length];
    private long mMedalTimestamp[] = new long[Medal.values().length];
    private int mGreeceVisitCount;
    public long mIslamicProfit;
    public long mPirateCapacity;
    public int mEscapeCountInOneDay;
    public int mCompromiseWithCrewCount;
    public int mWrongNavigationCountInOneDay;
    public int mWinPiratesCountInOneDay;
    public int mWinPiratesCount;
    private int mNightProfitCount;
    private int mDefaultNumGuards = DEFAULT_NUM_GUARDS;
    public boolean mSafeSail;
    public boolean mBdsTurkey;
    public boolean mBdsOlives;
    public boolean mAlwaysSleepAtMidnight;
    public boolean mAlwaysDeposit;
    public boolean mEconomicalSail;
    public long mValueAtStartOfDay = 0;
    public boolean mHeroDieMedal = false;
    public long mHighScore = 0;
    public long mHighCapacity = 0;
    public int[] mRank = {-1, -1};
    public FortuneTellerInformation mFortuneTellerInformation;
    public long[] mProgressSamples = new long[(SLEEP_TIME - START_HOUR + 1) * WeekDay.NUM_WEEK_DAYS];
    public int mNumProgressSamples;

    public class FortuneTellerInformation {
        public State state;
        public Goods goods;
        int price;
    };

    public static class ScoreTable {
        public int rank;
        public String name;
        public long score;
    };

    public enum ScoreType {
        HIGH_SCORE_TABLE_INDEX(0),
        HIGH_CAPACITY_TABLE_INDEX(1);
        public static final int SCORE_TYPES = 2;
        private final int value;
        ScoreType(int value){
            this.value = value;
        }
        public int getValue() {
            return value;
        }
        private int[] googleId = {R.string.leaderboard_highscore, R.string.leaderboard_capacity};
        public int getGoogleId() { return googleId[value]; }
    }

    public ScoreTable[][] mScoreTable = new ScoreTable[ScoreType.SCORE_TYPES][10];

    MarketDeal mMarketDeal;
    BankDeal mBankDeal;
    FixShipDeal mFixShipDeal;
    Sail mSail;
    public Tutorial mTutorial = new Tutorial(this);
    public State mSpecialPriceState;
    public Goods mSpecialPriceGoods;
    public boolean mIsSpecialPriceHigh;
    public long mFishBoatCollisionDamage;
    public long mGoodsUnitsToBurn = 0;
    public Goods mGoodsToBurn;
    public long mValueBeforeSail;
    public long mValueAfterSail;
    public boolean mEgyptWheatMedal = false;
    public boolean mTitanicMedal = false;
    public boolean mAllGoodMedal = false;
    public boolean mFogOfWarMedal = false;

    // For NewDayEvent.BIGGER_SHIP
    public boolean mIsBiggerShipForCash;
    public long mBiggerShipPrice;
    public Goods mBiggerShipPriceGoodType;
    public long mBiggerShipCapacity;

    // For NewDayEvent.MERCHANT
    public boolean mIsMerchantBuy;
    public long mMerchantPrice;
    public Goods mMerchantGoods;
    public long mMerchantUnits;

    public NegotiationType mNegotiationType;
    public int mLoseDayByStrike;

    public enum NewDayEvent {
        SPECIAL_PRICE,
        FISH_BOAT_COLLISION,
        FIRE,
        BIGGER_SHIP_OFFER,
        MERCHANT,
        STRIKE,
    };

    public enum NegotiationType {
        PIRATES,
        CREW,
    }

    public NewDayEvent mNewDayEvent;

    public long generateRandom(long bound) {
        return Math.abs(mRand.nextLong()) % Math.max(1, bound);
    }

    public float generateFloat() {
        return mRand.nextFloat();
    }

    public void clearInventory() {
        for (int i = 0 ; i < mInventory.length ; i++) {
            mInventory[i] = 0;
        }
    }

    public void startNewGame() {
        mPriceTable.generateRandomPrices();
        mCash = hasMedal(Medal.TREASURE_5) ? START_GAME_CASH_WITH_MEDAL : START_GAME_CASH;
        mBankDeposit = 0;
        mCurrentDay = WeekDay.SUNDAY;
        mCurrentHour = START_HOUR;
        mCurrentState = START_STATE;
        mWeatherState = START_WEATHER_STATE;
        mWeather = START_WEATHER;
        mDamage = START_DAMAGE;
        mCapacity = hasMedal(Medal.CAPACITY_3) ? START_CAPACITY_WITH_MEDAL : START_CAPACITY;
        clearInventory();
        mIsBankOperationTakesTime = true;
        mIsMarketOperationTakesTime = true;
        mIsFixOperationTakesTime = true;
        mIsFreeFixUsed = false;

        for (State state : State.values()) {
            mStatesVisitedToday[state.getValue()] = (state == START_STATE);
        }

        mIslamicProfit = 0;
        mPirateCapacity = 0;
        mEscapeCountInOneDay = 0;
        mCompromiseWithCrewCount = 0;
        mWrongNavigationCountInOneDay = 0;
        mWinPiratesCountInOneDay = 0;
        mWinPiratesCount = 0;
        mValueBeforeSail = 0;
        mValueAfterSail = 0;
        mBdsTurkey = true;
        mBdsOlives = true;
        mAlwaysDeposit = true;
        mHeroDieMedal = false;
        mAlwaysSleepAtMidnight = true;
        mEconomicalSail = true;
        mSafeSail = true;
        mNightProfitCount = 0;
        mValueAtStartOfDay = calculateTotalValue();
        mGreeceVisitCount = mStatesVisitedToday[State.GREECE.getValue()] ? 1 : 0;

        if (hasMedal(Medal.GREECE_VISITOR)) {
            updateSailDuration(State.GREECE, State.CYPRUS);
        }

        if (hasMedal(Medal.ISLAMIC_STATE)) {
            updateSailDuration(State.TURKEY, State.EGYPT);
        }

        mFortuneTellerInformation = null;

        mProgressSamples[0] = mCash;
        mNumProgressSamples = 1;
    }

    public void initMarketDeal(Goods goods) {
        mMarketDeal = new MarketDeal(goods, this);
    }

    public void applyMarketDeal() {
        if (mMarketDeal.mGoods == Goods.OLIVES) {
            mBdsOlives = false;
        }
        mCash = mMarketDeal.mCash;
        mInventory[mMarketDeal.mGoods.getValue()] = mMarketDeal.mGoodsUnits;
        mMarketDeal = null;
        if (mIsMarketOperationTakesTime) {
            mIsMarketOperationTakesTime = false;
            increaseHour(1);
        }
    }

    public void initBankDeal() {
        mBankDeal = new BankDeal(this);
    }

    public void applyBankDeal() {
        mCash = mBankDeal.mCash;
        mBankDeposit = mBankDeal.mDeposit;
        if (mIsBankOperationTakesTime) {
            increaseHour(1);
            mIsBankOperationTakesTime = false;
        }
    }

    public void initFixShipDeal() {
        mFixShipDeal = new FixShipDeal(this);
    }

    public void applyShipFixDeal() {
        mCash -= mFixShipDeal.mCurrentFix;
        mDamage -= mFixShipDeal.mCurrentFix;

        if (mIsFixOperationTakesTime) {
            mIsFixOperationTakesTime = false;
            increaseHour(1);
        }
    }

    public void applyFreeShipFixDeal() {
        mDamage = 0;
        mIsFreeFixUsed = true;

        if (mIsFixOperationTakesTime) {
            mIsFixOperationTakesTime = false;
            increaseHour(1);
        }
    }

    public void initSail(State destination) {
        mSail = new Sail(this, destination);
        mValueBeforeSail = calculateTotalValue();
        mValueAfterSail = mValueBeforeSail;
    }

    public void finishSail() {
        mAllGoodMedal = true;
        for (Goods goods : Goods.values()) {
            if (getInventory(goods) == 0) {
                mAllGoodMedal = false;
                break;
            }
            if (mPriceTable.getPrice(mCurrentState, goods) >= mPriceTable.getPrice(mSail.mDestination, goods)) {
                mAllGoodMedal = false;
                break;
            }
        }

        if ((mCurrentState == State.EGYPT && mSail.mDestination == State.TURKEY) ||
                (mCurrentState == State.TURKEY && mSail.mDestination == State.EGYPT)) {
            for (Goods goods : Goods.values()) {
                if (getInventory(goods) > 0) {
                    long diff = mPriceTable.getPrice(mSail.mDestination, goods) - mPriceTable.getPrice(mCurrentState, goods);
                    if (diff > 0) {
                        mIslamicProfit += diff * getInventory(goods);
                    }
                }
            }
        }

        mCurrentState = mSail.mDestination;
        increaseHour(getSailDuration(mSail.mSource, mCurrentState));
        if (!hasMedal(Medal.ECONOMICAL_SAIL)) {
            mIsBankOperationTakesTime = true;
        }
        mIsMarketOperationTakesTime = true;
        mIsFixOperationTakesTime = true;
        if (mCurrentState == State.GREECE) {
            if (!mStatesVisitedToday[mCurrentState.getValue()]) {
                mGreeceVisitCount++;
            }
        }
        mStatesVisitedToday[mCurrentState.getValue()] = true;
        mValueAfterSail = calculateTotalValue();

        if (mCurrentState == State.TURKEY) {
            mBdsTurkey = false;
        }
    }

    public int getSailDuration(State to) {
        return getSailDuration(mCurrentState, to);
    }

    public boolean canReachToDestinationBeforeSleepTime(State to) {
        return getSailDuration(to) + mCurrentHour < SLEEP_TIME;
    }

    public int getSailDuration(State from, State to) {
        return mSailDurations[from.getValue()][to.getValue()];
    }

    public int getEveningTime() {
        return EVENING_TIME + (hasMedal(Medal.GERMAN_TIME) ? 1 : 0);
    }

    public boolean isStillDayTimeAfterBankOperation() {
        if (!mIsBankOperationTakesTime) {
            return (mCurrentHour < getEveningTime());
        } else {
            return (mCurrentHour + 1 < getEveningTime());
        }
    }

    public boolean isStillDayTimeAfterMarketOperation() {
        if (!mIsMarketOperationTakesTime) {
            return (mCurrentHour < getEveningTime());
        } else {
            return (mCurrentHour + 1 < getEveningTime());
        }
    }

    public DayPart getDayPart() {
        if (mCurrentHour < getEveningTime()) {
            return DayPart.SUN_SHINES;
        }

        if (mCurrentHour < NIGHT_TIME) {
            return DayPart.EVENING;
        }

        return DayPart.NIGHT;
    }

    public void startNewDay() {
        int[] pricesBeforeNight = new int[Goods.NUM_GOODS_TYPES];
        for (Goods goods : Goods.values()) {
            pricesBeforeNight[goods.getValue()] =
                    mPriceTable.getPrice(mCurrentState, goods);
        }

        if (mCurrentHour != SLEEP_TIME) {
            mAlwaysSleepAtMidnight = false;
        }

        increaseHour(SLEEP_TIME - mCurrentHour + 1);

        mLoseDayByStrike = 0;
        mPriceTable.generateRandomPrices();

        if (mFortuneTellerInformation != null && mFortuneTellerInformation.price != 0) {
            mPriceTable.setPrice(mFortuneTellerInformation.state, mFortuneTellerInformation.goods, mFortuneTellerInformation.price);
        }
        mFortuneTellerInformation = null;

        mCurrentDay = WeekDay.values()[mCurrentDay.getValue() + 1];
        mWeather = Weather.values()[mRand.nextInt(Weather.NUM_WEATHER_TYPES)];
        mWeatherState = State.values()[mRand.nextInt(State.NUM_STATES)];

        if (mBankDeposit < 0.9 * calculateTotalValue()) {
            mAlwaysDeposit = false;
        }

        mBankDeposit = mBankDeposit * (100 + getBankNightlyInterest()) / 100;
        mIsBankOperationTakesTime = true;
        mIsMarketOperationTakesTime = true;
        mIsFixOperationTakesTime = true;

        for (State state : State.values()) {
            mStatesVisitedToday[state.getValue()] = (state == mCurrentState);
        }
        if (mCurrentState == State.GREECE) {
            mGreeceVisitCount++;
        }

        if (mCurrentState == State.EGYPT) {
            int wheatPriceInEgyptAtMorning = mPriceTable.getPrice(State.EGYPT, Goods.WHEAT);
            if (wheatPriceInEgyptAtMorning >= 2 * pricesBeforeNight[Goods.WHEAT.getValue()]) {
                if (getInventory(Goods.WHEAT) >= 10000) {
                    mEgyptWheatMedal = true;
                }
            }
        }

        boolean hasGoodsWithNightProfit = false;
        boolean hasGoodsWithoutNightProfit = false;
        for (Goods goods : Goods.values()) {
            if (getInventory(goods) > 0) {
                if (mPriceTable.getPrice(mCurrentState, goods) > pricesBeforeNight[goods.getValue()]) {
                    hasGoodsWithNightProfit = true;
                } else {
                    hasGoodsWithoutNightProfit = true;
                    break;
                }
            }
        }

        if (hasGoodsWithNightProfit && (!hasGoodsWithoutNightProfit)) {
            mNightProfitCount++;
        }

        mEscapeCountInOneDay = 0;
        mWrongNavigationCountInOneDay = 0;
        mWinPiratesCountInOneDay = 0;
        mValueAtStartOfDay = calculateTotalValue();

        refreshCurrentHourProgressSample();
    }

    private void generateFishBoatCollision() {
        long maxDamage = Math.min(calculateTotalValue(), mCapacity * mCapacity) / 5;
        mFishBoatCollisionDamage = generateRandom(maxDamage);
        mDamage += mFishBoatCollisionDamage;
        mNewDayEvent = NewDayEvent.FISH_BOAT_COLLISION;
    }

    protected Goods findGoodsWithMostUnits() {
        Goods foundGoods = Goods.COPPER;
        long maxUnits = 0;

        for (Goods goods : Goods.values()) {
            if (mInventory[goods.getValue()] > maxUnits) {
                maxUnits = mInventory[goods.getValue()];
                foundGoods = goods;
            }
        }

        return foundGoods;
    }

    // Return true if fire burn goods or false if nothing can be burned.
    private boolean generateFire() { ;
        mGoodsToBurn = findGoodsWithMostUnits();
        mGoodsUnitsToBurn = mInventory[mGoodsToBurn.getValue()];

        if (mGoodsUnitsToBurn > 0) {
            mGoodsUnitsToBurn = generateRandom(mGoodsUnitsToBurn / 2) + 1;
            mNewDayEvent = NewDayEvent.FIRE;
            removeGoodsFromInventory(mGoodsToBurn, mGoodsUnitsToBurn);
            refreshCurrentHourProgressSample();
            return true;
        } else {
            return false;
        }
    }

    public boolean generateBiggerShipDeal() {
        if (calculateTotalValue() - mBankDeposit == 0) {
            return false;
        }
        mBiggerShipPrice = mCash;
        mIsBiggerShipForCash = true;
        for (Goods goods : Goods.values()) {
            if (mPriceTable.getPrice(mCurrentState, goods) * mInventory[goods.getValue()] > mBiggerShipPrice) {
                mBiggerShipPrice = mPriceTable.getPrice(mCurrentState, goods) * mInventory[goods.getValue()];
                mBiggerShipPriceGoodType = goods;
                mIsBiggerShipForCash = false;
            }
        }
        if (!mIsBiggerShipForCash) {
            mBiggerShipPrice /= mPriceTable.getPrice(mCurrentState, mBiggerShipPriceGoodType);
        }

        mBiggerShipPrice = generateRandom(mBiggerShipPrice) + 1;

        long maxCapacityForCopper = calculateTotalValue() / mPriceTable.getPrice(mCurrentState, Goods.COPPER);
        mBiggerShipCapacity = ((generateRandom(maxCapacityForCopper + 1) + generateRandom(100) + 26) / 25) * 25;
        mNewDayEvent = NewDayEvent.BIGGER_SHIP_OFFER;
        return true;
    }

    public void acceptOffer() {
        switch (mNewDayEvent) {
            case MERCHANT:
                acceptMerchantDeal();
                break;
            case BIGGER_SHIP_OFFER:
                acceptBiggerShipDeal();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mNewDayEvent);
        }

        refreshCurrentHourProgressSample();
    }

    private void acceptMerchantDeal() {
        if (mIsMerchantBuy) {
            mCash += mMerchantPrice * mMerchantUnits;
            removeGoodsFromInventory(mMerchantGoods, mMerchantUnits);
        } else {
            mCash -= mMerchantPrice * mMerchantUnits;
            addGoodsToInventory(mMerchantGoods, mMerchantUnits);
        }
    }

    private void acceptBiggerShipDeal() {
        mCapacity += mBiggerShipCapacity;
        if (mIsBiggerShipForCash) {
            mCash -= mBiggerShipPrice;
        } else {
            removeGoodsFromInventory(mBiggerShipPriceGoodType, mBiggerShipPrice);
        }
    }

    public boolean generateMerchantDeal() {
        long availableValue = calculateTotalValue() - mBankDeposit;
        if (availableValue < MIN_VALUE_FOR_MERCHANT) {
            return false;
        }

        mIsMerchantBuy = (availableValue > mCash);
        if (mIsMerchantBuy) {
            mMerchantGoods = findGoodsWithMostUnits();
            mMerchantPrice = mMerchantGoods.generateRandomMerchant();
            mMerchantUnits = mInventory[mMerchantGoods.getValue()];
        } else {
            mMerchantGoods = Goods.generateRandomType();
            mMerchantPrice = mMerchantGoods.generateRandomMerchant();
            mMerchantUnits = generateRandom(mCash / mMerchantPrice) + 1;
        }

        mNewDayEvent = NewDayEvent.MERCHANT;

        return true;
    }

    public void generateNewDayEvent() {
        int random = mRand.nextInt(6);

        // Easy start. No fish boat before achieve 100,000
        if ((random == 0) && (hasMedal(Medal.TREASURE_2))) {
            generateFishBoatCollision();
            return;
        }

        // Easy start. No fire or strike before achieve 1,000,000
        if ((random == 1) && (hasMedal(Medal.TREASURE_3))) {
            if (mCurrentDay.getValue() < WeekDay.FRIDAY.getValue() && (
                    calculateTotalValue() - mBankDeposit > 0) ) {
                boolean skipStrike = (hasMedal(Medal.CREW_NEGOTIATOR)) && mRand.nextBoolean();
                if (!skipStrike) {
                    mNewDayEvent = NewDayEvent.STRIKE;
                    mNegotiationType = NegotiationType.CREW;
                    return;
                }
            } else if (generateFire()) {
                return;
            }
        }

        if (random == 2) {
            if (generateBiggerShipDeal()) {
                return;
            }
        }

        if (random == 3) {
            if (generateMerchantDeal()) {
                return;
            }
        }

        mNewDayEvent = NewDayEvent.SPECIAL_PRICE;

        mIsSpecialPriceHigh = mRand.nextInt(2) == 0;
        mSpecialPriceGoods = Goods.generateRandomGoods();
        mSpecialPriceState = State.GREECE;

        ArrayList<State> stateList = new ArrayList<State>();
        stateList.add(State.ISRAEL);
        stateList.add(State.CYPRUS);
        if (!hasMedal(Medal.EGYPT_WHEAT)) {
            stateList.add(State.EGYPT);
        }
        if (!hasMedal(Medal.BDS_TURKEY)) {
            stateList.add(State.TURKEY);
        }

        mSpecialPriceState = stateList.get(mRand.nextInt(stateList.size()));

        int specialPrice = mIsSpecialPriceHigh ? mSpecialPriceGoods.generateRandomHigh() :
                mSpecialPriceGoods.generateRandomLow();

        mPriceTable.setPrice(mSpecialPriceState, mSpecialPriceGoods, specialPrice);

        refreshCurrentHourProgressSample();
    }

    public long calculateInventoryValue() {
        long totalValue = 0;
        for (Goods goods : Goods.values()) {
            totalValue += mInventory[goods.getValue()] * mPriceTable.getPrice(mCurrentState, goods);
        }
        return totalValue;
    }

    public long calculateTotalValue() {
        return mCash + mBankDeposit + calculateInventoryValue();
    }

    public long calculateLoad() {
        long load = 0;
        for (Goods goods : Goods.values()) {
            load += mInventory[goods.getValue()];
        }
        return  load;
    }

    public long getInventory(Goods goods) {
        return mInventory[goods.getValue()];
    }

    public void addGoodsToInventory(Goods goods, long units) {
        mInventory[goods.getValue()] += units;
    }

    public void removeGoodsFromInventory(Goods goods, long units) {
        mInventory[goods.getValue()] -= units;
    }

    public boolean sendOffer(long goodsOffer[], long cashOffer) {
        long offerValue = cashOffer;
        for (Goods goods : Goods.values()) {
            offerValue += goodsOffer[goods.getValue()] * mPriceTable.getPrice(mCurrentState, goods);
        }

        int divider = (mNegotiationType == NegotiationType.PIRATES) ? 2 : 3;

        long desired = generateRandom((calculateTotalValue() - mBankDeposit) / divider);

        if (desired > offerValue) {
            if (mNegotiationType == NegotiationType.CREW) {
                mLoseDayByStrike = 2;
            }
            return false;
        }

        for (Goods goods : Goods.values()) {
            removeGoodsFromInventory(goods, goodsOffer[goods.getValue()]);
        }
        mCash -= cashOffer;

        if (mNegotiationType == NegotiationType.CREW) {
            mCompromiseWithCrewCount++;
        } else {
            disableWinPiratesCount();
        }

        return true;
    }

    public void findNewCrew() {
        mLoseDayByStrike = 1;
    }

    public boolean isDamagePreventSail() {
        return mDamage > mCapacity * mCapacity;
    }

    public boolean canGoToBank() {
        if (!mIsBankOperationTakesTime) {
            return true;
        }

        return (mCurrentHour < SLEEP_TIME);
    }

    public boolean canGoToMarket() {
        if (!mIsMarketOperationTakesTime) {
            return true;
        }
        return (mCurrentHour < SLEEP_TIME);
    }

    public boolean isShipBroken() {
        return (mDamage > 0);
    }

    public boolean isEnoughTimeForFixShip() {
        if (!isFixOperationTakesTime()) {
            return true;
        }

        return (mCurrentHour < SLEEP_TIME);
    }

    public boolean canFixShip() {
        if (!isShipBroken()) {
            return  false;
        }

        return isEnoughTimeForFixShip();
    }

    public int getBankNightlyInterest() {
        if (mActivity.mIsStartTutorialActive) {
            return BANK_NIGHTLY_INTEREST;
        }
        return hasMedal(Medal.FEDERAL_RESERVE) ? BANK_NIGHTLY_INTEREST_WITH_MEDAL : BANK_NIGHTLY_INTEREST;
    }

    public boolean isBankOperationTakesTime() {
        return mIsBankOperationTakesTime;
    }

    public boolean isMarketOperationTakesTime() {
        return mIsMarketOperationTakesTime;
    }

    public boolean isFixOperationTakesTime() {
        return mIsFixOperationTakesTime;
    }

    protected final String getString(@StringRes int resId) {
        return mActivity.getString(resId);
    }

    public void storeInvalidState(SharedPreferences.Editor editor) {
        editor.putBoolean(getString(R.string.mInvalidState), true);
    }

    public void storeState(SharedPreferences.Editor editor) {
        editor.putBoolean(getString(R.string.mInvalidState), false);
        editor.putLong(getString(R.string.mCash), mCash);
        editor.putLong(getString(R.string.mBankDeposit), mBankDeposit);
        editor.putInt(getString(R.string.mCurrentDay), mCurrentDay.getValue());
        editor.putInt(getString(R.string.mCurrentHour), mCurrentHour);
        editor.putInt(getString(R.string.mCurrentState), mCurrentState.getValue());
        editor.putInt(getString(R.string.mWeatherState), mWeatherState.getValue());
        editor.putInt(getString(R.string.mWeather), mWeather.getValue());
        editor.putLong(getString(R.string.mDamage), mDamage);
        editor.putLong(getString(R.string.mCapacity), mCapacity);
        editor.putLong(getString(R.string.mHighScore), mHighScore);
        editor.putLong(getString(R.string.mHighCapacity), mHighCapacity);
        editor.putInt(getString(R.string.mEscapeCountInOneDay), mEscapeCountInOneDay);
        editor.putInt(getString(R.string.mCompromiseWithCrewCount), mCompromiseWithCrewCount);
        editor.putInt(getString(R.string.mWrongNavigationCountInOneDay), mWrongNavigationCountInOneDay);
        editor.putInt(getString(R.string.mWinPiratesCountInOneDay), mWinPiratesCountInOneDay);
        editor.putInt(getString(R.string.mWinPiratesCount), mWinPiratesCount);
        editor.putLong(getString(R.string.mValueAtStartOfDay), mValueAtStartOfDay);
        editor.putLong(getString(R.string.mIslamicProfit), mIslamicProfit);
        editor.putLong(getString(R.string.mPirateCapacity), mPirateCapacity);
        editor.putInt(getString(R.string.mGreeceVisitCount), mGreeceVisitCount);
        editor.putInt(getString(R.string.mNightProfitCount), mNightProfitCount);
        editor.putInt(getString(R.string.mDefaultNumGuards), mDefaultNumGuards);
        editor.putBoolean(getString(R.string.mBdsTurkey), mBdsTurkey);
        editor.putBoolean(getString(R.string.mBdsOlives), mBdsOlives);
        editor.putBoolean(getString(R.string.mAlwaysDeposit), mAlwaysDeposit);
        editor.putBoolean(getString(R.string.mAlwaysSleepAtMidnight), mAlwaysSleepAtMidnight);
        editor.putBoolean(getString(R.string.mEconomicalSail), mEconomicalSail);
        editor.putBoolean(getString(R.string.mSafeSail), mSafeSail);
        editor.putBoolean(getString(R.string.mIsBankOperationTakesTime), mIsBankOperationTakesTime);
        editor.putBoolean(getString(R.string.mIsMarketOperationTakesTime), mIsMarketOperationTakesTime);
        editor.putBoolean(getString(R.string.mIsFixOperationTakesTime), mIsFixOperationTakesTime);
        editor.putBoolean(getString(R.string.mIsFreeFixUsed), mIsFreeFixUsed);
        editor.putBoolean(getString(R.string.mIsFastAnimation), mActivity.mIsFastAnimation);
        editor.putBoolean(getString(R.string.mIsSoundEnable), mActivity.mIsSoundEnable);
        editor.putBoolean(getString(R.string.mIsGoogleSignIn), mActivity.mIsGoogleSignIn);
        editor.putBoolean(getString(R.string.mIsStartTutorialActive), mActivity.mIsStartTutorialActive);
        editor.putString(getString(R.string.mCurrentLanguage), mActivity.mCurrentLanguage);
        editor.putInt(getString(R.string.mNumProgressSamples), mNumProgressSamples);

        if (mFortuneTellerInformation != null) {
            editor.putInt(getString(R.string.mFortuneTellerState), mFortuneTellerInformation.state.getValue());
            editor.putInt(getString(R.string.mFortuneTellerGoods), mFortuneTellerInformation.goods.getValue());
            editor.putInt(getString(R.string.mFortuneTellerPrice), mFortuneTellerInformation.price);
        } else {
            editor.remove(getString(R.string.mFortuneTellerState));
            editor.remove(getString(R.string.mFortuneTellerGoods));
            editor.remove(getString(R.string.mFortuneTellerPrice));
        }

        StringBuilder str = new StringBuilder();
        for (Goods goods : Goods.values()) {
            str.append(mInventory[goods.getValue()]).append(",");
        }
        editor.putString(getString(R.string.mInventory), str.toString());

        str = new StringBuilder();
        for (State state : State.values()) {
            for (Goods goods : Goods.values()) {
                str.append(mPriceTable.getPrice(state, goods)).append(",");
            }
        }
        editor.putString(getString(R.string.mPriceTable), str.toString());

        str = new StringBuilder();
        for (Medal medal : Medal.values()) {
            str.append(mIsMedalAchieved[medal.getValue()]).append(",");
        }
        editor.putString(getString(R.string.mIsMedalAchieved), str.toString());

        str = new StringBuilder();
        for (Medal medal : Medal.values()) {
            str.append(mMedalTimestamp[medal.getValue()]).append(",");
        }
        editor.putString(getString(R.string.mMedalTimestamp), str.toString());

        str = new StringBuilder();
        for (boolean bool : mStatesVisitedToday) {
            str.append(bool).append(",");
        }
        editor.putString(getString(R.string.mStatesVisitedToday), str.toString());

        str = new StringBuilder();
        for (int i = 0; i < mNumProgressSamples; i++) {
            str.append(mProgressSamples[i]).append(",");
        }
        editor.putString(getString(R.string.mProgressSamples), str.toString());
    }

    private long getLongOrInt(SharedPreferences sharedPref, int stringId, int defaultValue) {
        try {
            return sharedPref.getLong(getString(stringId), defaultValue);
        } catch (Exception e) {
            return sharedPref.getInt(getString(stringId), defaultValue);
        }
    }

    public void restoreState( SharedPreferences sharedPref) {
        String savedString = sharedPref.getString(getString(R.string.mIsMedalAchieved), "");
        StringTokenizer st = new StringTokenizer(savedString, ",");
        for (Medal medal : Medal.values()) {
            if (st.hasMoreTokens()){
                mIsMedalAchieved[medal.getValue()] = Boolean.parseBoolean(st.nextToken());
            } else {
                mIsMedalAchieved[medal.getValue()] = false;
            }
        }

        savedString = sharedPref.getString(getString(R.string.mMedalTimestamp), "");
        st = new StringTokenizer(savedString, ",");
        for (Medal medal : Medal.values()) {
            if (st.hasMoreTokens()){
                mMedalTimestamp[medal.getValue()] = Long.parseLong(st.nextToken());
            }
        }

        mActivity.mCurrentLanguage = sharedPref.getString(getString(R.string.mCurrentLanguage), null);

        mActivity.mIsFastAnimation = sharedPref.getBoolean(getString(R.string.mIsFastAnimation), false);
        mActivity.mIsSoundEnable = sharedPref.getBoolean(getString(R.string.mIsSoundEnable), true);
        mActivity.mIsGoogleSignIn = sharedPref.getBoolean(getString(R.string.mIsGoogleSignIn), false);
        mActivity.mIsStartTutorialActive = sharedPref.getBoolean(getString(R.string.mIsStartTutorialActive), false);
        mCurrentHour = sharedPref.getInt(getString(R.string.mCurrentHour), 0);
        if (mCurrentHour == 0) {
            // When starting after first installation - this will be 0, so start tutorial.
            // When loading from previous version, this won't be 0, so do not start tutorial.
            mActivity.mIsStartTutorialActive = true;
        }

        boolean invalidState = sharedPref.getBoolean(getString(R.string.mInvalidState), false);

        if (mActivity.mIsStartTutorialActive || invalidState) {
            startNewGame();
            // First time enter the game - start tutorial by mActivity later.
            return;
        }

        mCash = getLongOrInt(sharedPref, R.string.mCash, START_GAME_CASH);
        mBankDeposit = getLongOrInt(sharedPref, R.string.mBankDeposit, 0);
        mCurrentDay = WeekDay.values()[sharedPref.getInt(getString(R.string.mCurrentDay), 0)];
        mCurrentState = State.values()[sharedPref.getInt(getString(R.string.mCurrentState), State.ISRAEL.getValue())];
        mWeatherState = State.values()[sharedPref.getInt(getString(R.string.mWeatherState), State.ISRAEL.getValue())];
        mWeather = Weather.values()[sharedPref.getInt(getString(R.string.mWeather), Weather.GOOD_SAILING.getValue())];
        mDamage = getLongOrInt(sharedPref, R.string.mDamage, 0);
        mCapacity = getLongOrInt(sharedPref, R.string.mCapacity, START_CAPACITY);
        mHighScore = getLongOrInt(sharedPref, R.string.mHighScore, 0);
        mHighCapacity = getLongOrInt(sharedPref, R.string.mHighCapacity, 0);
        mEscapeCountInOneDay = sharedPref.getInt(getString(R.string.mEscapeCountInOneDay), 0);
        mCompromiseWithCrewCount = sharedPref.getInt(getString(R.string.mCompromiseWithCrewCount), 0);
        mWrongNavigationCountInOneDay = sharedPref.getInt(getString(R.string.mWrongNavigationCountInOneDay), 0);
        mWinPiratesCountInOneDay = sharedPref.getInt(getString(R.string.mWinPiratesCountInOneDay), 0);
        mWinPiratesCount = sharedPref.getInt(getString(R.string.mWinPiratesCount), -10000);
        mValueAtStartOfDay = getLongOrInt(sharedPref, R.string.mValueAtStartOfDay, -10000);
        mIslamicProfit = getLongOrInt(sharedPref, R.string.mIslamicProfit, 0);
        mPirateCapacity = getLongOrInt(sharedPref, R.string.mPirateCapacity, 0);
        mGreeceVisitCount = sharedPref.getInt(getString(R.string.mGreeceVisitCount), 0);
        mNightProfitCount = sharedPref.getInt(getString(R.string.mNightProfitCount), 0);
        mDefaultNumGuards = sharedPref.getInt(getString(R.string.mDefaultNumGuards), DEFAULT_NUM_GUARDS);
        mBdsTurkey = sharedPref.getBoolean(getString(R.string.mBdsTurkey), false);
        mBdsOlives = sharedPref.getBoolean(getString(R.string.mBdsOlives), false);
        mAlwaysSleepAtMidnight = sharedPref.getBoolean(getString(R.string.mAlwaysSleepAtMidnight), false);
        mAlwaysDeposit = sharedPref.getBoolean(getString(R.string.mAlwaysDeposit), false);
        mEconomicalSail = sharedPref.getBoolean(getString(R.string.mEconomicalSail), false);
        mSafeSail = sharedPref.getBoolean(getString(R.string.mSafeSail), false);
        mIsBankOperationTakesTime = sharedPref.getBoolean(getString(R.string.mIsBankOperationTakesTime), true);
        mIsMarketOperationTakesTime = sharedPref.getBoolean(getString(R.string.mIsMarketOperationTakesTime), true);
        mIsFixOperationTakesTime = sharedPref.getBoolean(getString(R.string.mIsFixOperationTakesTime), true);
        mIsFreeFixUsed = sharedPref.getBoolean(getString(R.string.mIsFreeFixUsed), false);
        mNumProgressSamples = sharedPref.getInt(getString(R.string.mNumProgressSamples), 0);

        savedString = sharedPref.getString(getString(R.string.mInventory), "");
        st = new StringTokenizer(savedString, ",");
        for (Goods goods : Goods.values()) {
            if (st.hasMoreTokens()){
                mInventory[goods.getValue()] = Long.parseLong(st.nextToken());
            }
        }

        savedString = sharedPref.getString(getString(R.string.mPriceTable), "");
        st = new StringTokenizer(savedString, ",");
        for (State state : State.values()) {
            for (Goods goods : Goods.values()) {
                mPriceTable.setPrice(state, goods, Integer.parseInt(st.nextToken()));
            }
        }

        savedString = sharedPref.getString(getString(R.string.mStatesVisitedToday), "");
        st = new StringTokenizer(savedString, ",");
        for (int i = 0 ; i < mStatesVisitedToday.length; i ++) {
            if (st.hasMoreTokens()){
                mStatesVisitedToday[i] = Boolean.parseBoolean(st.nextToken());
            } else {
                mStatesVisitedToday[i] = false;
            }
        }

        mValueBeforeSail = 0;
        mValueAfterSail = 0;

        if (hasMedal(Medal.GREECE_VISITOR)) {
            updateSailDuration(State.GREECE, State.CYPRUS);
        }

        if (hasMedal(Medal.ISLAMIC_STATE)) {
            updateSailDuration(State.TURKEY, State.EGYPT);
        }

        try {
            State state = State.values()[sharedPref.getInt(getString(R.string.mFortuneTellerState), State.NUM_STATES)];
            Goods goods = Goods.values()[sharedPref.getInt(getString(R.string.mFortuneTellerGoods), Goods.NUM_GOODS_TYPES)];
            int price = sharedPref.getInt(getString(R.string.mFortuneTellerPrice), 0);

            mFortuneTellerInformation = new FortuneTellerInformation();
            mFortuneTellerInformation.state = state;
            mFortuneTellerInformation.goods = goods;
            mFortuneTellerInformation.price = price;

        } catch (Exception e) {
            mFortuneTellerInformation = null;
        }

        savedString = sharedPref.getString(getString(R.string.mProgressSamples), "");
        st = new StringTokenizer(savedString, ",");
        for (int i = 0 ; i < mNumProgressSamples; i ++) {
            if (st.hasMoreTokens()){
                mProgressSamples[i] = Long.parseLong(st.nextToken());
            } else {
                mProgressSamples[i] = 0;
            }
        }
    }

    public boolean hasMedal(Medal medal) {
        return mIsMedalAchieved[medal.getValue()];
    }

    public Medal acquireNewMedal() {
        Medal medal = whichMedalShouldAcquire();
        if (medal != null) {
            mIsMedalAchieved[medal.getValue()] = true;
        }
        return medal;
    }

    public void restoreMedal(Medal medal, long timestamp) {
        mIsMedalAchieved[medal.getValue()] = true;
        mMedalTimestamp[medal.getValue()] = timestamp;
    }

    public long getMedalTimeStamp(Medal medal) {
        return mMedalTimestamp[medal.getValue()];
    }

    private Medal whichMedalShouldAcquire() {
        long totalValue = calculateTotalValue();

        if ((!hasMedal(Medal.TREASURE_1)) && (mCash >= 10000)) {
            return Medal.TREASURE_1;
        }

        if ((!hasMedal(Medal.TREASURE_2)) && (mCash >= 100000)) {
            return Medal.TREASURE_2;
        }

        if ((!hasMedal(Medal.TREASURE_3)) && (mCash >= 1000000)) {
            return Medal.TREASURE_3;
        }

        if ((!hasMedal(Medal.TREASURE_4)) && (mCash >= 2000000)) {
            return Medal.TREASURE_4;
        }

        if ((!hasMedal(Medal.TREASURE_5)) && (mCash >= 10000000)) {
            return Medal.TREASURE_5;
        }

        if ((!hasMedal(Medal.IRON_BANK)) && (mCurrentHour == START_HOUR) && (mBankDeposit >= 55000)) {
            return Medal.IRON_BANK;
        }

        if ((!hasMedal(Medal.CAPACITY_1)) && (mCapacity >= 200)) {
            return Medal.CAPACITY_1;
        }

        if ((!hasMedal(Medal.CAPACITY_2)) && (mCapacity >= 500)) {
            return Medal.CAPACITY_2;
        }

        if ((!hasMedal(Medal.CAPACITY_3)) && (mCapacity >= 1000)) {
            return Medal.CAPACITY_3;
        }

        if (!hasMedal(Medal.AROUND_THE_WORLD)) {
            int count = 0;
            for (boolean stateVisited : mStatesVisitedToday) {
                if (!stateVisited) {
                    break;
                }
                count++;
            }
            if (count == mStatesVisitedToday.length) {
                return Medal.AROUND_THE_WORLD;
            }
        }

        if (!hasMedal(Medal.ESCAPE) && mEscapeCountInOneDay >= 3) {
            return Medal.ESCAPE;
        }

        if (!hasMedal(Medal.CREW_NEGOTIATOR) && mCompromiseWithCrewCount >= 2) {
            return Medal.CREW_NEGOTIATOR;
        }

        if (!hasMedal(Medal.WIND) && mWrongNavigationCountInOneDay >= 3) {
            return Medal.WIND;
        }

        if (!hasMedal(Medal.YOUNG_FIGHTER) && mWinPiratesCountInOneDay >= 1) {
            return Medal.YOUNG_FIGHTER;
        }

        if (!hasMedal(Medal.TIRED_FIGHTER) && mWinPiratesCountInOneDay >= 3) {
            return Medal.TIRED_FIGHTER;
        }

        if (!hasMedal(Medal.ALWAYS_FIGHTER) && mWinPiratesCount >= 10) {
            return Medal.ALWAYS_FIGHTER;
        }

        if (!hasMedal(Medal.EGYPT_WHEAT) && mEgyptWheatMedal) {
            return Medal.EGYPT_WHEAT;
        }

        if (!hasMedal(Medal.DOUBLE_SAIL) && (mValueAfterSail >= 2 * mValueBeforeSail) && (mValueAfterSail > 0)) {
            return Medal.DOUBLE_SAIL;
        }

        if (!hasMedal(Medal.TITANIC) && mTitanicMedal) {
            return Medal.TITANIC;
        }

        if (!hasMedal(Medal.ALL_GOODS) && mAllGoodMedal) {
            return Medal.ALL_GOODS;
        }

        if (!hasMedal(Medal.BDS_TURKEY) && mBdsTurkey && mCash >= 1000000) {
            return Medal.BDS_TURKEY;
        }

        if (!hasMedal(Medal.GOOD_DAY_1) && (mValueAtStartOfDay > 0) && (mValueAtStartOfDay * 2 <=  totalValue)) {
            return Medal.GOOD_DAY_1;
        }

        if (!hasMedal(Medal.GOOD_DAY_2) && (mValueAtStartOfDay > 0) && (mValueAtStartOfDay * 5 <=  totalValue)) {
            return Medal.GOOD_DAY_2;
        }

        if (!hasMedal(Medal.GOOD_DAY_3) && (mValueAtStartOfDay > 0) && (mValueAtStartOfDay * 8 <=  totalValue)) {
            return Medal.GOOD_DAY_3;
        }

        if (!hasMedal(Medal.GREECE_VISITOR) && (mGreeceVisitCount == 7) && (totalValue >= 1000000)) {
            updateSailDuration(State.GREECE, State.CYPRUS);
            return Medal.GREECE_VISITOR;
        }

        if (!hasMedal(Medal.FEDERAL_RESERVE) && (mCurrentDay.isLastDay()) &&
                (mAlwaysDeposit) && (totalValue >= 1000000)) {
            return Medal.FEDERAL_RESERVE;
        }

        if ((!hasMedal(Medal.FAST_EXIT)) && (mCurrentDay.getValue() <= WeekDay.TUESDAY.getValue() && mCash >= 1000000)) {
            return Medal.FAST_EXIT;
        }

        if (!hasMedal(Medal.DIET_MERCHANT) && mBdsOlives && mCash >= 1000000) {
            return Medal.DIET_MERCHANT;
        }

        if (!hasMedal(Medal.HERO_DIE) && mHeroDieMedal) {
            return Medal.HERO_DIE;
        }

        if (!hasMedal(Medal.GERMAN_TIME) && mAlwaysSleepAtMidnight && (totalValue >= 1000000) &&
                mCurrentDay.isLastDay() && mCurrentHour == SLEEP_TIME) {
            return Medal.GERMAN_TIME;
        }

        if (!hasMedal(Medal.ECONOMICAL_SAIL) && mEconomicalSail && totalValue >= 2000000) {
            return Medal.ECONOMICAL_SAIL;
        }

        if (!hasMedal(Medal.FOG_OF_WAR) && mFogOfWarMedal) {
            return Medal.FOG_OF_WAR;
        }

        if (!hasMedal(Medal.NIGHT_MERCHANT) && mNightProfitCount == WeekDay.NUM_WEEK_DAYS - 1 && totalValue >= 10000000) {
            return Medal.NIGHT_MERCHANT;
        }

        if (!hasMedal(Medal.SAFE_SAIL) && mSafeSail && totalValue >= 10000000) {
            return Medal.SAFE_SAIL;
        }

        if (!hasMedal(Medal.ISLAMIC_STATE) && mIslamicProfit >= 5000000) {
            updateSailDuration(State.TURKEY, State.EGYPT);
            return Medal.ISLAMIC_STATE;
        }

        if (!hasMedal(Medal.PIRATE_FLEET) && mPirateCapacity >= 10000) {
            return Medal.PIRATE_FLEET;
        }

        return null;
    }

    public float getMedalProgress(Medal medal) {
        long totalValue = calculateTotalValue();

        switch (medal) {
            case TREASURE_1:
                return ((float)mCash) / 10000f;
            case TREASURE_2:
                return ((float)mCash) / 100000f;
            case TREASURE_3:
            case BDS_TURKEY:
            case FAST_EXIT:
            case DIET_MERCHANT:
                return ((float)mCash) / 1000000f;
            case TREASURE_4:
                return ((float)mCash) / 2000000f;
            case TREASURE_5:
                return ((float)mCash) / 10000000f;
            case CAPACITY_1:
                return ((float)mCapacity) / 200f;
            case CAPACITY_2:
                return ((float)mCapacity) / 500f;
            case CAPACITY_3:
                return ((float)mCapacity) / 1000;
            case AROUND_THE_WORLD:
                float count = 0;
                for (boolean stateVisited : mStatesVisitedToday) {
                    count += stateVisited ? 1 : 0;
                }
                return count / mStatesVisitedToday.length;
            case ESCAPE:
                return (float)mEscapeCountInOneDay / 3f;
            case CREW_NEGOTIATOR:
                return (float)mCompromiseWithCrewCount / 2f;
            case WIND:
                return (float)mWrongNavigationCountInOneDay / 3f;
            case TIRED_FIGHTER:
                return (float)mWinPiratesCountInOneDay / 3f;
            case ALWAYS_FIGHTER:
                return (float)mWinPiratesCount / 10f;
            case GOOD_DAY_1:
                return (mValueAtStartOfDay > 0) ? ((float)totalValue / (mValueAtStartOfDay * 2)) : 0f;
            case GOOD_DAY_2:
                return (mValueAtStartOfDay > 0) ? ((float)totalValue / (mValueAtStartOfDay * 5)) : 0f;
            case GOOD_DAY_3:
                return (mValueAtStartOfDay > 0) ? ((float)totalValue / (mValueAtStartOfDay * 8)) : 0f;
            case GREECE_VISITOR:
            case FEDERAL_RESERVE:
            case GERMAN_TIME:
                return (float)totalValue / 1000000f;
            case ECONOMICAL_SAIL:
                return (float)totalValue / 2000000f;
            case NIGHT_MERCHANT:
            case SAFE_SAIL:
                return (float)totalValue / 10000000f;
            case ISLAMIC_STATE:
                return (float)mIslamicProfit / 5000000f;
            case PIRATE_FLEET:
                return (float)mPirateCapacity / 10000f;
            default:
                return 0f;
        }
    }

    public boolean canGetThisMedal(Medal medal) {
        switch (medal) {
            default:
                return true;

            case ALWAYS_FIGHTER:
                return mWinPiratesCount >= 0;

            case BDS_TURKEY:
                return mBdsTurkey;

            case GREECE_VISITOR:
                if (mStatesVisitedToday[State.GREECE.getValue()]) {
                    return mGreeceVisitCount == mCurrentDay.getValue() + 1;
                } else {
                    return mGreeceVisitCount == mCurrentDay.getValue();
                }

            case FEDERAL_RESERVE:
                return mAlwaysDeposit;

            case FAST_EXIT:
                return mCurrentDay.getValue() <= WeekDay.TUESDAY.getValue();

            case DIET_MERCHANT:
                return mBdsOlives;

            case GERMAN_TIME:
                return mAlwaysSleepAtMidnight;

            case ECONOMICAL_SAIL:
                return mEconomicalSail;

            case NIGHT_MERCHANT:
                return mNightProfitCount >= mCurrentDay.getValue();

            case SAFE_SAIL:
                return mSafeSail;
        }
    }

    public void disableWinPiratesCount() {
        mWinPiratesCount = -10000;
    }

    public void startSail() {
        if (calculateTotalValue() / 2 < mCash) {
            mEconomicalSail = false;
        }

        mCash -= mSail.mTotalGuardShipsCost;
    }

    public void setNewHighScore(long highScore) {
        mHighScore = Math.max(mHighScore, highScore);
    }

    public void setNewHighCapacity(long highCapacity) {
        mHighCapacity = Math.max(mHighCapacity, highCapacity);
    }

    public void setUserScore(int rank, String name, long score, ScoreType scoreType) {
        if (rank <  1) {
            // This should not happen, but in any case that google sends bad value.
            return;
        }
        ScoreTable scoreTable;
        if (rank < 6) {
            scoreTable = mScoreTable[scoreType.getValue()][rank - 1];
            scoreTable.name = name;
            scoreTable.score = score;
            scoreTable.rank = rank;
        }

        mRank[scoreType.getValue()] = rank;

        if (scoreType == ScoreType.HIGH_SCORE_TABLE_INDEX) {
            setNewHighScore(score);
        } else {
            setNewHighCapacity(score);
        }
    }

    public void setTopScore(int rank, String name, long score, ScoreType scoreType) {
        if (rank < 1) {
            // This should not happen, but in any case that google sends bad value.
            return;
        }

        ScoreTable scoreTable = mScoreTable[scoreType.getValue()][rank - 1];

        if (rank > 5) {
            if (scoreTable.rank != rank) {
                // maybe already got centered score which is more important.
                return;
            }
        }
        scoreTable.name = name;
        scoreTable.score = score;
        scoreTable.rank = rank;
    }

    public void setCenterScore(int rank, String name, long score, int index, ScoreType scoreType) {
        if (rank < 1) {
            // This should not happen, but in any case that google sends bad value.
            return;
        }

        ScoreTable oneScore;
        ScoreTable[] scoreTable = mScoreTable[scoreType.getValue()];
        if (rank < 6) {
            oneScore = scoreTable[rank - 1];
        } else {
            if (rank - index < 6) {
                // got ranks 3,4,5,6,7 which should sit in cells 2,3,4,5,6
                oneScore = scoreTable[rank - 1];
            } else {
                // got ranks 8,9,10,11,12 which should sit in cells 5,6,7,8,9
                oneScore = scoreTable[5 + index];
            }
        }

        oneScore.name = name;
        oneScore.score = score;
        oneScore.rank = rank;
    }

    public void callFortuneTeller() {
        if (mFortuneTellerInformation != null) {
            return;
        }

        //Fortune teller works only at evening
        if (getDayPart() != DayPart.EVENING) {
            return;
        }

        mFortuneTellerInformation = new FortuneTellerInformation();
        mFortuneTellerInformation.goods = Goods.generateRandomType();
        mFortuneTellerInformation.state = State.generateRandomType();
        mFortuneTellerInformation.price = 0;
    }

    public boolean canAskFortuneTeller() {
        return (mFortuneTellerInformation != null) && (mFortuneTellerInformation.price == 0) && (getDayPart() == DayPart.EVENING);
    }

    public boolean  isInfoFromFortuneTellerAvailable() {
        return (mFortuneTellerInformation != null) && (mFortuneTellerInformation.price != 0);
    }

    public void askFortuneTeller() {
        mFortuneTellerInformation.price = mFortuneTellerInformation.goods.generateRandom();

        if ((mFortuneTellerInformation.state == State.GREECE) ||
                ((mFortuneTellerInformation.state == State.EGYPT) && hasMedal(Medal.EGYPT_WHEAT)) ||
                ((mFortuneTellerInformation.state == State.TURKEY) && hasMedal(Medal.BDS_TURKEY))) {
            mFortuneTellerInformation.price = mPriceTable.setSpecialPrice(mFortuneTellerInformation.price);
        }

        increaseHour(1);
    }

    // fortune teller will appear only at evening.
    public boolean isFortuneTellButtonShown() {
        if (mActivity.mIsStartTutorialActive) {
            return false;
        }
        return (getDayPart() != DayPart.SUN_SHINES) && hasMedal(Medal.NIGHT_MERCHANT);
    }

    public float maxValuePartForGuards() {
        int maxGuards = getDefaultNumGuards();
        if (hasMedal(Medal.ALWAYS_FIGHTER) && maxGuards > 0) {
            maxGuards--;
        }

        return Sail.DEFAULT_GUARD_COST_PERCENT * maxGuards / 100;
    }

    public void setSailDamage(long damage) {
        mDamage += damage;
        mSafeSail = false;
    }

    public boolean isFreeFixButtonVisible() {
        return (!mIsFreeFixUsed) && (!mActivity.mIsStartTutorialActive) && hasMedal(Medal.SAFE_SAIL);
    }

    public void setDefaultNumGuards(int numGuards) {
        mDefaultNumGuards = numGuards;
    }

    public int getDefaultNumGuards() {
        return mDefaultNumGuards;
    }

    public boolean canSelectAttackPiratesPrize() {
        return hasMedal(Medal.PIRATE_FLEET) && (!mActivity.mIsStartTutorialActive);
    }

    private void increaseHour(int numHoursToAdd) {
        mCurrentHour += numHoursToAdd;

        long shipValue = calculateTotalValue();
        while (numHoursToAdd > 0) {
            mProgressSamples[mNumProgressSamples++] = shipValue;
            numHoursToAdd--;
        }

        if (mCurrentHour > SLEEP_TIME) {
            mCurrentHour = START_HOUR;
        }
    }

    private void refreshCurrentHourProgressSample() {
        if (mNumProgressSamples == 0) {
            return;
        }

        long shipValue = calculateTotalValue();
        mProgressSamples[mNumProgressSamples - 1] = shipValue;
    }

    public void loseDaysByStrike() {
        mCurrentDay = mCurrentDay.add(mLoseDayByStrike);
        while (mLoseDayByStrike > 0) {
            increaseHour(SLEEP_TIME - START_HOUR + 1);
            mLoseDayByStrike--;
        }
    }
}