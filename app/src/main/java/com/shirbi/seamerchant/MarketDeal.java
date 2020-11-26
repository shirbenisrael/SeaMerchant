package com.shirbi.seamerchant;

public class MarketDeal {
    Goods mGoods;
    int mGoodsUnits;
    int mPrice;
    int mCash;
    int mCapacityForThisGoods;
    int mMaxUnitsToHold;

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
}