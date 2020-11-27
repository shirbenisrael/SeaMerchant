package com.shirbi.seamerchant;

public class Logic {
    private static final int START_GAME_CASH = 5000;
    private static final int START_HOUR = 6;
    private static final Weather START_WEATHER = Weather.GOOD_SAILING;
    private static final State START_STATE = State.ISRAEL;
    private static final int START_CAPACITY = 100;
    private static final int EVENING_TIME = 16;
    private static final int NIGHT_TIME = 21;

    private int mSailDurations[][] = {
            {0, 3, 6, 3, 0}, // From Egypt
            {3, 0, 3, 3, 0}, // From Israel
            {6, 3, 0, 3, 0}, // From Turkey
            {3, 3, 3, 0, 6}, // From Cyprus
            {0, 0, 0, 6, 0}  // From Greece
    };

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
    Sail mSail;

    public int mInventory[];

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
    }
}
