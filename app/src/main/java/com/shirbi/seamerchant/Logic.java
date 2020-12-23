package com.shirbi.seamerchant;

import android.content.SharedPreferences;

import androidx.annotation.StringRes;

import java.util.Random;
import java.util.StringTokenizer;

public class Logic {
    private static final int START_GAME_CASH = 5000;
    public static final int START_HOUR = 6;
    private static final Weather START_WEATHER = Weather.GOOD_SAILING;
    private static final State START_STATE = State.ISRAEL;
    private static final int START_CAPACITY = 100;
    public static final int EVENING_TIME = 17;
    public static final int NIGHT_TIME = 21;
    public static final int SLEEP_TIME = 24;
    private static final int MIN_VALUE_FOR_MERCHANT = 4500;
    public static final int BANK_NIGHTLY_INTEREST = 10;

    private MainActivity mActivity;
    public Logic(MainActivity activity) {
        mActivity = activity;
    }

    private int mSailDurations[][] = {
            { 0,  3,  6,  3, -1}, // From Egypt
            { 3,  0,  3,  3, -1}, // From Israel
            { 6,  3,  0,  3, -1}, // From Turkey
            { 3,  3,  3,  0,  6}, // From Cyprus
            {-1, -1, -1,  6,  0}  // From Greece
    };

    private Random mRand = new Random();

    // Variable need to store
    public PriceTable mPriceTable = new PriceTable();
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
        mCash = START_GAME_CASH;
        mBankDeposit = 0;
        mCurrentDay = WeekDay.SUNDAY;
        mCurrentHour = START_HOUR;
        mCurrentState = START_STATE;
        mWeatherState = START_STATE;
        mWeather = START_WEATHER;
        mDamage = 0;
        mCapacity = START_CAPACITY;
        for (int i = 0 ; i < mInventory.length ; i++) {
            mInventory[i] = 0;
        }
        mIsBankOperationTakesTime = true;
        mIsMarketOperationTakesTime = true;
    }

    public void initMarketDeal(Goods goods) {
        mMarketDeal = new MarketDeal(goods, this);
    }

    public void applyMarketDeal() {
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
        mCurrentHour++;
    }

    public void initSail(State destination) {
        mSail = new Sail(this, destination);
    }

    public void finishSail() {
        mCurrentState = mSail.mDestination;
        mCurrentHour += getSailDuration(mSail.mSource, mCurrentState);
        mIsBankOperationTakesTime = true;
        mIsMarketOperationTakesTime = true;
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

    public DayPart getDayPart() {
        if (mCurrentHour < EVENING_TIME) {
            return DayPart.SUN_SHINES;
        }

        if (mCurrentHour < NIGHT_TIME) {
            return DayPart.EVENING;
        }

        return DayPart.NIGHT;
    }

    public void startNewDay() {
        mLoseDayByStrike = 0;
        mPriceTable.generateRandomPrices();
        mCurrentHour = START_HOUR;
        mCurrentDay = WeekDay.values()[mCurrentDay.getValue() + 1];
        mWeather = Weather.values()[mRand.nextInt(Weather.NUM_WEATHER_TYPES)];
        mWeatherState = State.values()[mRand.nextInt(State.NUM_STATES)];
        mBankDeposit = mBankDeposit * (100 + BANK_NIGHTLY_INTEREST) / 100;
        mIsBankOperationTakesTime = true;
        mIsMarketOperationTakesTime = true;
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
                mNewDayEvent = NewDayEvent.STRIKE;
                mNegotiationType = NegotiationType.CREW;
                return;
            }
            if (generateFire()) {
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
        while (mSpecialPriceState == State.GREECE) {
            mSpecialPriceState = State.generateRandomState();
        }

        int specialPrice = mIsSpecialPriceHigh ? mSpecialPriceGoods.generateRandomHigh() :
                mSpecialPriceGoods.generateRandomLow();

        mPriceTable.setPrice(mSpecialPriceState, mSpecialPriceGoods, specialPrice);
    }

    public int calculateTotalValue() {
        int totalValue = mCash + mBankDeposit;
        for (Goods goods : Goods.values()) {
            totalValue += mInventory[goods.getValue()] * mPriceTable.getPrice(mCurrentState, goods);
        }
        return totalValue;
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

        return true;
    }

    public void findNewCrew() {
        mLoseDayByStrike = 1;
    }

    public boolean isDamagePreventSail() {
        return mDamage > mCapacity * mCapacity;
    }

    public boolean canGoToMarket() {
        if (mCurrentHour >= SLEEP_TIME) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isShipBroken() {
        return (mDamage > 0);
    }
    public boolean canFixShip() {
        return (isShipBroken() && (mCurrentHour < SLEEP_TIME));
    }

    public int getBankNightlyInterest() {
        return BANK_NIGHTLY_INTEREST;
    }

    public boolean isBankOperationTakesTime() {
        return mIsBankOperationTakesTime;
    }

    public boolean isMarketOperationTakesTime() {
        return mIsMarketOperationTakesTime;
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

        editor.putBoolean(getString(R.string.mIsBankOperationTakesTime), mIsBankOperationTakesTime);
        editor.putBoolean(getString(R.string.mIsMarketOperationTakesTime), mIsMarketOperationTakesTime);

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
    }

    public void restoreState( SharedPreferences sharedPref) {
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

        mIsBankOperationTakesTime = sharedPref.getBoolean(getString(R.string.mIsBankOperationTakesTime), true);
        mIsMarketOperationTakesTime = sharedPref.getBoolean(getString(R.string.mIsMarketOperationTakesTime), true);

        String savedString = sharedPref.getString(getString(R.string.mInventory), "");
        StringTokenizer st = new StringTokenizer(savedString, ",");
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
    }
}
