package com.shirbi.seamerchant;

import java.util.Random;

public class Logic {
    private static final int START_GAME_CASH = 5000;
    private static final int START_HOUR = 6;
    private static final Weather START_WEATHER = Weather.GOOD_SAILING;
    private static final State START_STATE = State.ISRAEL;
    private static final int START_CAPACITY = 100;
    private static final int EVENING_TIME = 16;
    private static final int NIGHT_TIME = 21;
    private static final int MIN_VALUE_FOR_MERCHANT = 4500;

    private int mSailDurations[][] = {
            {0, 3, 6, 3, 0}, // From Egypt
            {3, 0, 3, 3, 0}, // From Israel
            {6, 3, 0, 3, 0}, // From Turkey
            {3, 3, 3, 0, 6}, // From Cyprus
            {0, 0, 0, 6, 0}  // From Greece
    };

    private Random mRand = new Random();
    public PriceTable mPriceTable = new PriceTable();
    public int mCash;
    public int mBankDeposit;
    public WeekDay mCurrentDay;
    public int mCurrentHour;
    public State mCurrentState;
    public State mWeatherState;
    public Weather mWeather;
    public int mDamage;
    public int mCapacity;
    MarketDeal mMarketDeal;
    BankDeal mBankDeal;
    Sail mSail;
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

    public int mInventory[];

    public enum NewDayEvent {
        SPECIAL_PRICE,
        FISH_BOAT_COLLISION,
        FIRE,
        BIGGER_SHIP_OFFER,
        MERCHANT,
    };

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
        mInventory = new int[Goods.NUM_GOODS_TYPES];
        for (int i = 0 ; i < mInventory.length ; i++) {
            mInventory[i] = 0;
        }
    }

    public void initMarketDeal(Goods goods) {
        mMarketDeal = new MarketDeal(goods, this);
    }

    public void applyMarketDeal() {
        mCash = mMarketDeal.mCash;
        mInventory[mMarketDeal.mGoods.getValue()] = mMarketDeal.mGoodsUnits;
        mMarketDeal = null;
        mCurrentHour++;
    }

    public void initBankDeal() {
        mBankDeal = new BankDeal(this);
    }

    public void applyBankDeal() {
        mCash = mBankDeal.mCash;
        mBankDeposit = mBankDeal.mDeposit;
        mCurrentHour++;
    }

    public void initSail(State destination) {
        mSail = new Sail(this, destination);
    }

    public void finishSail() {
        mCurrentState = mSail.mDestination;
        mCurrentHour += getSailDuration(mSail.mSource, mCurrentState);
    }

    public int getSailDuration(State to) {
        return getSailDuration(mCurrentState, to);
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

        mPriceTable.generateRandomPrices();
        mCurrentHour = START_HOUR;
        mCurrentDay = WeekDay.values()[mCurrentDay.getValue() + 1];
        mWeather = Weather.values()[mRand.nextInt(Weather.NUM_WEATHER_TYPES)];
        mWeatherState = State.values()[mRand.nextInt(State.NUM_STATES)];
    }

    private void generateFishBoatCollision() {
        int maxDamage = Math.min(calculateTotalValue(), mCapacity * mCapacity) / 5;
        mFishBoatCollisionDamage = mRand.nextInt(maxDamage);
        mDamage += mFishBoatCollisionDamage;
        mNewDayEvent = NewDayEvent.FISH_BOAT_COLLISION;
    }

    private Goods findGoodsWithMostUnits() {
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

    private int calculateTotalValue() {
        int totalValue = mCash + mBankDeposit;
        for (Goods goods : Goods.values()) {
            totalValue += mInventory[goods.getValue()] * mPriceTable.getPrice(mCurrentState, goods);
        }
        return totalValue;
    }

    public void addGoodsToInventory(Goods goods, int units) {
        mInventory[goods.getValue()] += units;
    }

    public void removeGoodsFromInventory(Goods goods, int units) {
        mInventory[goods.getValue()] -= units;
    }
}
