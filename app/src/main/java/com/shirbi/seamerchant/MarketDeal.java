package com.shirbi.seamerchant;

public class MarketDeal {
    Goods mGoods;
    int mGoodsUnits;
    int mPrice;
    int mCash;
    int mCapacityForThisGoods;
    int mMaxUnitsToHold;
    int mMaxUnitsWithEnoughGuardShips;
    int mPercentageOfValueForEnoughGuardShips;

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

        float guardsForInventoryPart = 0.1f;

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

        int valueForGuardPrice = logic.calculateInventoryValue() + logic.mCash;
        int guardPrice = (int)(valueForGuardPrice * guardsForInventoryPart);

        int totalMoneyCanBeUsedForGuards = mCash + getGoodsValue();
        mMaxUnitsWithEnoughGuardShips = Math.max(0, totalMoneyCanBeUsedForGuards - guardPrice) / mPrice;

        mMaxUnitsToHold = mGoodsUnits + mCash / mPrice;
    }

    public int getGoodsValue() {
        return mGoodsUnits * mPrice;
    }

    public void setGoods(int units) {
        if (units > mGoodsUnits) {
            addGoods(units - mGoodsUnits);
        } else {
            removeGoods(mGoodsUnits - units);
        }
    }

    public void addGoods(int units) {
        int totalPrice = units * mPrice;
        if (mCash >= totalPrice) {
            mGoodsUnits += units;
            mCash -= totalPrice;
        } else {
            buyAll();
        }
    }

    public void removeGoods(int units) {
        if (mGoodsUnits >= units) {
            mGoodsUnits -= units;
            mCash += units * mPrice;;
        } else {
            sellAll();
        }
    }

    public void buyAll() {
        int unitsToBuy = mCash / mPrice;
        addGoods(unitsToBuy);
    }

    public void sellAll() {
        removeGoods(mGoodsUnits);
    }

    public void fillCapacity() {
        int unitsToAdd = mCapacityForThisGoods - mGoodsUnits;
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