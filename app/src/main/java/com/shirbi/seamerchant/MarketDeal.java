package com.shirbi.seamerchant;

public class MarketDeal {
    Goods mGoods;
    long mGoodsUnits;
    int mPrice;
    long mCash;
    long mCapacityForThisGoods;
    long mMaxUnitsToHold;
    long mMaxUnitsWithEnoughGuardShips;

    public MarketDeal(Goods goods, Logic logic) {
        mGoods = goods;
        mGoodsUnits = logic.mInventory[goods.getValue()];
        mPrice = logic.mPriceTable.getPrice(logic.mCurrentState, goods);
        mCash = logic.mCash;
        mCapacityForThisGoods = logic.mCapacity;
        for (Goods otherGoods : Goods.values()) {
            if (otherGoods != goods) {
                mCapacityForThisGoods -= logic.mInventory[otherGoods.getValue()];
            }
        }

        float guardsForInventoryPart = logic.maxValuePartForGuards();

        if (logic.mCurrentState == logic.mWeatherState) {
            // if we know we are in bad weather, then we know guards will cost more.
            guardsForInventoryPart *= Sail.getWeatherGuardCostMultiplyFromHere(logic.mWeather);
        } else if(logic.mCurrentState == State.GREECE && logic.mWeatherState == State.CYPRUS) {
            // if we know we are going to bad weather, then we know guards will cost more.
            guardsForInventoryPart *= Sail.getWeatherGuardCostMultiplyToThere(logic.mWeather);
        }

        if (!logic.isStillDayTimeAfterMarketOperation()) {
            guardsForInventoryPart *= Sail.NIGHT_SAIL_GUARD_COST_PERCENT_MULTIPLY;
        }

        long valueForGuardPrice = logic.calculateInventoryValue() + logic.mCash;
        long guardPrice = (long)(valueForGuardPrice * (double)guardsForInventoryPart);

        long totalMoneyCanBeUsedForGuards = mCash + getGoodsValue();
        mMaxUnitsWithEnoughGuardShips = Math.max(0, totalMoneyCanBeUsedForGuards - guardPrice) / mPrice;

        mMaxUnitsToHold = mGoodsUnits + mCash / mPrice;
    }

    public long getGoodsValue() {
        return mGoodsUnits * mPrice;
    }

    public void setGoods(long units) {
        if (units > mGoodsUnits) {
            addGoods(units - mGoodsUnits);
        } else {
            removeGoods(mGoodsUnits - units);
        }
    }

    public void addGoods(long units) {
        long totalPrice = units * mPrice;
        if (mCash >= totalPrice) {
            mGoodsUnits += units;
            mCash -= totalPrice;
        } else {
            buyAll();
        }
    }

    public void removeGoods(long units) {
        if (mGoodsUnits >= units) {
            mGoodsUnits -= units;
            mCash += units * mPrice;;
        } else {
            sellAll();
        }
    }

    public void buyAll() {
        long unitsToBuy = mCash / mPrice;
        addGoods(unitsToBuy);
    }

    public void sellAll() {
        removeGoods(mGoodsUnits);
    }

    public void fillCapacity() {
        long unitsToAdd = mCapacityForThisGoods - mGoodsUnits;
        if (unitsToAdd > 0) {
            addGoods(unitsToAdd);
        } else {
            removeGoods(-unitsToAdd);
        }
    }

    public void leaveCashForGuards() {
        setGoods(mMaxUnitsWithEnoughGuardShips);
    }
}