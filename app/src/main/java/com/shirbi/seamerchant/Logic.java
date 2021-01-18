package com.shirbi.seamerchant;

import android.content.SharedPreferences;

import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class Logic {
    private static final int START_GAME_CASH = 5000;
    private static final int START_GAME_CASH_WITH_MEDAL = 10000;
    public static final int START_HOUR = 6;
    private static final Weather START_WEATHER = Weather.GOOD_SAILING;
    private static final State START_STATE = State.ISRAEL;
    private static final int START_CAPACITY = 100;
    private static final int START_CAPACITY_WITH_MEDAL = 200;
    public static final int EVENING_TIME = 17;
    public static final int NIGHT_TIME = 21;
    public static final int SLEEP_TIME = 24;
    private static final int MIN_VALUE_FOR_MERCHANT = 4500;
    public static final int BANK_NIGHTLY_INTEREST = 10;
    public static final int BANK_NIGHTLY_INTEREST_WITH_MEDAL = 15;

    private MainActivity mActivity;
    public Logic(MainActivity activity) {
        mActivity = activity;

        for (int i = 0; i < mScoreTable.length; i++) {
            mScoreTable[i] = new ScoreTable();
            mScoreTable[i].rank = i + 1;
            mScoreTable[i].name = "Shir";
            mScoreTable[i].score = 100 - (i * i);
        }
    }

    private int mSailDurations[][] = {
            { 0,  3,  6,  3, -1}, // From Egypt
            { 3,  0,  3,  3, -1}, // From Israel
            { 6,  3,  0,  3, -1}, // From Turkey
            { 3,  3,  3,  0,  6}, // From Cyprus
            {-1, -1, -1,  6,  0}  // From Greece
    };

    private void updateGreeceSailDuration() {
        mSailDurations[State.GREECE.getValue()][State.CYPRUS.getValue()] = 5;
        mSailDurations[State.CYPRUS.getValue()][State.GREECE.getValue()] = 5;
    }

    private Random mRand = new Random();

    // Variable need to store
    public PriceTable mPriceTable = new PriceTable(this);
    public int mInventory[] = new int[Goods.NUM_GOODS_TYPES];
    public int mCash;
    public int mBankDeposit;
    public WeekDay mCurrentDay;
    public int mCurrentHour;
    public State mCurrentState;
    public State mWeatherState;
    public Weather mWeather;
    public int mDamage;
    public int mCapacity;
    public boolean mIsBankOperationTakesTime;
    public boolean mIsMarketOperationTakesTime;
    public boolean mIsFixOperationTakesTime;
    private boolean mIsMedalAchieved[] = new boolean[Medal.NUM_MEDAL_TYPES];
    private boolean mStatesVisitedToday[] = new boolean[State.values().length];
    private int mGreeceVisitCount;
    public int mEscapeCountInOneDay;
    public int mCompromiseWithCrewCount;
    public int mWrongNavigationCountInOneDay;
    public int mWinPiratesCountInOneDay;
    public int mWinPiratesCount;
    public boolean mBdsTurkey;
    public boolean mBdsOlives;
    public boolean mAlwaysSleepAtMidnight;
    public boolean mAlwaysDeposit;
    public boolean mEconomicalSail;
    public int mValueAtStartOfDay = 0;
    public boolean mHeroDieMedal = false;
    public int mHighScore = 0;
    public int mRank = -1;

    public class ScoreTable {
        public int rank;
        public String name;
        public int score;
    };

    public ScoreTable[] mScoreTable = new ScoreTable[10];

    MarketDeal mMarketDeal;
    BankDeal mBankDeal;
    FixShipDeal mFixShipDeal;
    Sail mSail;
    public Tutorial mTutorial = new Tutorial(this);
    public State mSpecialPriceState;
    public Goods mSpecialPriceGoods;
    public boolean mIsSpecialPriceHigh;
    public int mFishBoatCollisionDamage;
    public int mGoodsUnitsToBurn = 0;
    public Goods mGoodsToBurn;
    public int mValueBeforeSail;
    public int mValueAfterSail;
    public boolean mEgyptWheatMedal = false;
    public boolean mTitanicMedal = false;
    public boolean mAllGoodMedal = false;

    // For NewDayEvent.BIGGER_SHIP
    public boolean mIsBiggerShipForCash;
    public int mBiggerShipPrice;
    public Goods mBiggerShipPriceGoodType;
    public int mBiggerShipCapacity;

    // For NewDayEvent.MERCHANT
    public boolean mIsMerchantBuy;
    public int mMerchantPrice;
    public Goods mMerchantGoods;
    public int mMerchantUnits;

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

    public void startNewGame() {
        mPriceTable.generateRandomPrices();
        mCash = hasMedal(Medal.TREASURE_5) ? START_GAME_CASH_WITH_MEDAL : START_GAME_CASH;
        mBankDeposit = 0;
        mCurrentDay = WeekDay.SUNDAY;
        mCurrentHour = START_HOUR;
        mCurrentState = START_STATE;
        mWeatherState = START_STATE;
        mWeather = START_WEATHER;
        mDamage = 0;
        mCapacity = hasMedal(Medal.CAPACITY_3) ? START_CAPACITY_WITH_MEDAL : START_CAPACITY;
        for (int i = 0 ; i < mInventory.length ; i++) {
            mInventory[i] = 0;
        }
        mIsBankOperationTakesTime = true;
        mIsMarketOperationTakesTime = true;
        mIsFixOperationTakesTime = true;

        for (State state : State.values()) {
            mStatesVisitedToday[state.getValue()] = (state == START_STATE);
        }

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
        mValueAtStartOfDay = calculateTotalValue();
        mGreeceVisitCount = mStatesVisitedToday[State.GREECE.getValue()] ? 1 : 0;

        if (hasMedal(Medal.GREECE_VISITOR)) {
            updateGreeceSailDuration();
        }
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
            mCurrentHour++;
        }
    }

    public void initBankDeal() {
        mBankDeal = new BankDeal(this);
    }

    public void applyBankDeal() {
        mCash = mBankDeal.mCash;
        mBankDeposit = mBankDeal.mDeposit;
        if (mIsBankOperationTakesTime) {
            mCurrentHour++;
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
            mCurrentHour++;
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
        mCurrentState = mSail.mDestination;
        mCurrentHour += getSailDuration(mSail.mSource, mCurrentState);
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
        int wheatPriceInEgyptBeforeNight = mPriceTable.getPrice(State.EGYPT, Goods.WHEAT);
        mLoseDayByStrike = 0;
        mPriceTable.generateRandomPrices();
        if (mCurrentHour != SLEEP_TIME) {
            mAlwaysSleepAtMidnight = false;
        }
        mCurrentHour = START_HOUR;
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
            if (wheatPriceInEgyptAtMorning >= 2 * wheatPriceInEgyptBeforeNight) {
                if (getInventory(Goods.WHEAT) >= 10000) {
                    mEgyptWheatMedal = true;
                }
            }
        }

        mEscapeCountInOneDay = 0;
        mWrongNavigationCountInOneDay = 0;
        mWinPiratesCountInOneDay = 0;
        mValueAtStartOfDay = calculateTotalValue();
    }

    private void generateFishBoatCollision() {
        int maxDamage = Math.min(calculateTotalValue(), mCapacity * mCapacity) / 5;
        mFishBoatCollisionDamage = mRand.nextInt(maxDamage);
        mDamage += mFishBoatCollisionDamage;
        mNewDayEvent = NewDayEvent.FISH_BOAT_COLLISION;
    }

    protected Goods findGoodsWithMostUnits() {
        Goods foundGoods = Goods.COPPER;
        int maxUnits = 0;

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
            mGoodsUnitsToBurn = mRand.nextInt(mGoodsUnitsToBurn / 2) + 1;
            mNewDayEvent = NewDayEvent.FIRE;
            removeGoodsFromInventory(mGoodsToBurn, mGoodsUnitsToBurn);
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

        mBiggerShipPrice = mRand.nextInt(mBiggerShipPrice) + 1;

        int maxCapacityForCopper = calculateTotalValue() / mPriceTable.getPrice(mCurrentState, Goods.COPPER);
        mBiggerShipCapacity = ((mRand.nextInt(maxCapacityForCopper + 1) + mRand.nextInt(100) + 26) / 25) * 25;
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
        int availableValue = calculateTotalValue() - mBankDeposit;
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
            mMerchantUnits = mRand.nextInt(mCash / mMerchantPrice) + 1;
        }

        mNewDayEvent = NewDayEvent.MERCHANT;

        return true;
    }

    public void generateNewDayEvent() {
        int random = mRand.nextInt(6);

        if (random == 0) {
            generateFishBoatCollision();
            return;
        }

        if (random == 1) {
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
    }

    public int calculateInventoryValue() {
        int totalValue = 0;
        for (Goods goods : Goods.values()) {
            totalValue += mInventory[goods.getValue()] * mPriceTable.getPrice(mCurrentState, goods);
        }
        return totalValue;
    }

    public int calculateTotalValue() {
        return mCash + mBankDeposit + calculateInventoryValue();
    }

    public int calculateLoad() {
        int load = 0;
        for (Goods goods : Goods.values()) {
            load += mInventory[goods.getValue()];
        }
        return  load;
    }

    public int getInventory(Goods goods) {
        return mInventory[goods.getValue()];
    }

    public void addGoodsToInventory(Goods goods, int units) {
        mInventory[goods.getValue()] += units;
    }

    public void removeGoodsFromInventory(Goods goods, int units) {
        mInventory[goods.getValue()] -= units;
    }

    public boolean sendOffer(int goodsOffer[], int cashOffer) {
        int offerValue = cashOffer;
        for (Goods goods : Goods.values()) {
            offerValue += goodsOffer[goods.getValue()] * mPriceTable.getPrice(mCurrentState, goods);
        }

        int divider = (mNegotiationType == NegotiationType.PIRATES) ? 2 : 3;

        int desired = mRand.nextInt((calculateTotalValue() - mBankDeposit) / divider);

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

    public void storeState( SharedPreferences.Editor editor) {
        editor.putInt(getString(R.string.mCash), mCash);
        editor.putInt(getString(R.string.mBankDeposit), mBankDeposit);
        editor.putInt(getString(R.string.mCurrentDay), mCurrentDay.getValue());
        editor.putInt(getString(R.string.mCurrentHour), mCurrentHour);
        editor.putInt(getString(R.string.mCurrentState), mCurrentState.getValue());
        editor.putInt(getString(R.string.mWeatherState), mWeatherState.getValue());
        editor.putInt(getString(R.string.mWeather), mWeather.getValue());
        editor.putInt(getString(R.string.mDamage), mDamage);
        editor.putInt(getString(R.string.mCapacity), mCapacity);
        editor.putInt(getString(R.string.mHighScore), mHighScore);
        editor.putInt(getString(R.string.mEscapeCountInOneDay), mEscapeCountInOneDay);
        editor.putInt(getString(R.string.mCompromiseWithCrewCount), mCompromiseWithCrewCount);
        editor.putInt(getString(R.string.mWrongNavigationCountInOneDay), mWrongNavigationCountInOneDay);
        editor.putInt(getString(R.string.mWinPiratesCountInOneDay), mWinPiratesCountInOneDay);
        editor.putInt(getString(R.string.mWinPiratesCount), mWinPiratesCount);
        editor.putInt(getString(R.string.mValueAtStartOfDay), mValueAtStartOfDay);
        editor.putInt(getString(R.string.mGreeceVisitCount), mGreeceVisitCount);
        editor.putBoolean(getString(R.string.mBdsTurkey), mBdsTurkey);
        editor.putBoolean(getString(R.string.mBdsOlives), mBdsOlives);
        editor.putBoolean(getString(R.string.mAlwaysDeposit), mAlwaysDeposit);
        editor.putBoolean(getString(R.string.mAlwaysSleepAtMidnight), mAlwaysSleepAtMidnight);
        editor.putBoolean(getString(R.string.mEconomicalSail), mEconomicalSail);
        editor.putBoolean(getString(R.string.mIsBankOperationTakesTime), mIsBankOperationTakesTime);
        editor.putBoolean(getString(R.string.mIsMarketOperationTakesTime), mIsMarketOperationTakesTime);
        editor.putBoolean(getString(R.string.mIsFixOperationTakesTime), mIsFixOperationTakesTime);
        editor.putBoolean(getString(R.string.mIsSoundEnable), mActivity.mIsSoundEnable);
        editor.putBoolean(getString(R.string.mIsGoogleSignIn), mActivity.mIsGoogleSignIn);

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
        for (boolean bool : mStatesVisitedToday) {
            str.append(bool).append(",");
        }
        editor.putString(getString(R.string.mStatesVisitedToday), str.toString());
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

        mActivity.mIsSoundEnable = sharedPref.getBoolean(getString(R.string.mIsSoundEnable), true);
        mActivity.mIsGoogleSignIn = sharedPref.getBoolean(getString(R.string.mIsGoogleSignIn), false);

        mCurrentHour = sharedPref.getInt(getString(R.string.mCurrentHour), 0);
        if (mCurrentHour == 0) {
            startNewGame();
            return;
        }

        mCash = sharedPref.getInt(getString(R.string.mCash), START_GAME_CASH);
        mBankDeposit = sharedPref.getInt(getString(R.string.mBankDeposit), 0);
        mCurrentDay = WeekDay.values()[sharedPref.getInt(getString(R.string.mCurrentDay), 0)];
        mCurrentState = State.values()[sharedPref.getInt(getString(R.string.mCurrentState), State.ISRAEL.getValue())];
        mWeatherState = State.values()[sharedPref.getInt(getString(R.string.mWeatherState), State.ISRAEL.getValue())];
        mWeather = Weather.values()[sharedPref.getInt(getString(R.string.mWeather), Weather.GOOD_SAILING.getValue())];
        mWeather = Weather.values()[sharedPref.getInt(getString(R.string.mWeather), Weather.GOOD_SAILING.getValue())];
        mDamage = sharedPref.getInt(getString(R.string.mDamage), 0);
        mCapacity = sharedPref.getInt(getString(R.string.mCapacity), START_CAPACITY);
        mHighScore = sharedPref.getInt(getString(R.string.mHighScore), 0);
        mEscapeCountInOneDay = sharedPref.getInt(getString(R.string.mEscapeCountInOneDay), 0);
        mCompromiseWithCrewCount = sharedPref.getInt(getString(R.string.mCompromiseWithCrewCount), 0);
        mWrongNavigationCountInOneDay = sharedPref.getInt(getString(R.string.mWrongNavigationCountInOneDay), 0);
        mWinPiratesCountInOneDay = sharedPref.getInt(getString(R.string.mWinPiratesCountInOneDay), 0);
        mWinPiratesCount = sharedPref.getInt(getString(R.string.mWinPiratesCount), -10000);
        mValueAtStartOfDay = sharedPref.getInt(getString(R.string.mValueAtStartOfDay), -10000);
        mGreeceVisitCount = sharedPref.getInt(getString(R.string.mGreeceVisitCount), 0);
        mBdsTurkey = sharedPref.getBoolean(getString(R.string.mBdsTurkey), false);
        mBdsOlives = sharedPref.getBoolean(getString(R.string.mBdsOlives), false);
        mAlwaysSleepAtMidnight = sharedPref.getBoolean(getString(R.string.mAlwaysSleepAtMidnight), false);
        mAlwaysDeposit = sharedPref.getBoolean(getString(R.string.mAlwaysDeposit), false);
        mEconomicalSail = sharedPref.getBoolean(getString(R.string.mEconomicalSail), false);
        mIsBankOperationTakesTime = sharedPref.getBoolean(getString(R.string.mIsBankOperationTakesTime), true);
        mIsMarketOperationTakesTime = sharedPref.getBoolean(getString(R.string.mIsMarketOperationTakesTime), true);
        mIsFixOperationTakesTime = sharedPref.getBoolean(getString(R.string.mIsFixOperationTakesTime), true);

        savedString = sharedPref.getString(getString(R.string.mInventory), "");
        st = new StringTokenizer(savedString, ",");
        for (Goods goods : Goods.values()) {
            if (st.hasMoreTokens()){
                mInventory[goods.getValue()] = Integer.parseInt(st.nextToken());
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
            updateGreeceSailDuration();
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

    public void restoreMedal(Medal medal) {
        mIsMedalAchieved[medal.getValue()] = true;
    }

    private Medal whichMedalShouldAcquire() {
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

        if (!hasMedal(Medal.GOOD_DAY_1) && (mValueAtStartOfDay > 0) && (mValueAtStartOfDay * 2 <=  calculateTotalValue())) {
            return Medal.GOOD_DAY_1;
        }

        if (!hasMedal(Medal.GOOD_DAY_2) && (mValueAtStartOfDay > 0) && (mValueAtStartOfDay * 5 <=  calculateTotalValue())) {
            return Medal.GOOD_DAY_2;
        }

        if (!hasMedal(Medal.GOOD_DAY_3) && (mValueAtStartOfDay > 0) && (mValueAtStartOfDay * 8 <=  calculateTotalValue())) {
            return Medal.GOOD_DAY_3;
        }

        if (!hasMedal(Medal.GREECE_VISITOR) && (mGreeceVisitCount == 7) && (calculateTotalValue() >= 1000000)) {
            updateGreeceSailDuration();
            return Medal.GREECE_VISITOR;
        }

        if (!hasMedal(Medal.FEDERAL_RESERVE) && (mCurrentDay.isLastDay()) &&
                (mAlwaysDeposit) && (calculateTotalValue() >= 1000000)) {
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

        if (!hasMedal(Medal.GERMAN_TIME) && mAlwaysSleepAtMidnight && (calculateTotalValue() >= 1000000) &&
                mCurrentDay.isLastDay() && mCurrentHour == SLEEP_TIME) {
            return Medal.GERMAN_TIME;
        }

        if (!hasMedal(Medal.ECONOMICAL_SAIL) && mEconomicalSail && calculateTotalValue() >= 2000000) {
            return Medal.ECONOMICAL_SAIL;
        }

        return null;
    }

    public void disableWinPiratesCount() {
        mWinPiratesCount = -10000;
    }

    public void startSail() {
        if (calculateTotalValue() / 2 < mCash) {
            mEconomicalSail = false;
        }

        mCash -= mSail.mGuardShipCost * mSail.mSelectedNumGuardShips;
    }

    public void setNewHighScore(int highScore) {
        mHighScore = Math.max(mHighScore, highScore);
    }

    public void setUserScore(int rank, String name, int score) {
        ScoreTable scoreTable;
        if (rank < 6) {
            scoreTable = mScoreTable[rank - 1];
            scoreTable.name = name;
            scoreTable.score = score;
            scoreTable.rank = rank;
        }

        mRank = rank;
    }

    public void setTopScore(int rank, String name, int score) {
        ScoreTable scoreTable = mScoreTable[rank - 1];
        scoreTable.name = name;
        scoreTable.score = score;
        scoreTable.rank = rank;
    }

    public void setCenterScore(int rank, String name, int score, int index) {
        ScoreTable scoreTable;
        if (rank < 6) {
            scoreTable = mScoreTable[rank - 1];
        } else {
            if (rank - index < 6) {
                // got ranks 3,4,5,6,7 which should sit in cells 2,3,4,5,6
                scoreTable = mScoreTable[rank - 1];
            } else {
                // got ranks 8,9,10,11,12 which should sit in cells 5,6,7,8,9
                scoreTable = mScoreTable[5 + index];
            }
        }

        scoreTable.name = name;
        scoreTable.score = score;
        scoreTable.rank = rank;
    }
}
