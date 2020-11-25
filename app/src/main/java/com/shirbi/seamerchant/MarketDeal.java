package com.shirbi.seamerchant;

public class MarketDeal {
    Goods mGoods;
    int mGoodsUnits;
    int mPrice;
    int mCash;

    public MarketDeal(Goods goods, Logic logic) {
        mGoods = goods;
        mGoodsUnits = 0;
        mPrice = logic.mPriceTable.getPrice(logic.mCurrentState, goods);
        mCash = logic.mCash;
    }

    public int getGoodsValue() {
        return mGoodsUnits * mPrice;
    }

    public void addGoods(int units) {
        int totalPrice = units * mPrice;
        if (mCash >= totalPrice) {
            mGoodsUnits += units;
            mCash -= totalPrice;
        }
    }

    public void removeGoods(int units) {
        if (mGoodsUnits >= units) {
            mGoodsUnits -= units;
            mCash += units * mPrice;;
        }
    }

    public void buyAll() {
        int unitsToBuy = mCash / mPrice;
        addGoods(unitsToBuy);
    }

    public void sellAll() {
        removeGoods(mGoodsUnits);
    }
}