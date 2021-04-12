package com.shirbi.seamerchant;

public class Tutorial {
    Logic mLogic;
    public Goods mGoodsToDeal;
    public State mStateToSail;

    public Tutorial(Logic logic) {
        mLogic = logic;
    }

    private State findPlaceToSellGoods(Goods goods) {
        State bestState = mLogic.mCurrentState;
        for (State state : State.values()) {
            if (mLogic.getSailDuration(state) <= 0) {
                continue;
            }

            // 1 hour for sell and maybe 1 more hour to buy first.
            if (mLogic.getSailDuration(state) + mLogic.mCurrentHour + 1 +
                    (mLogic.mIsMarketOperationTakesTime ? 1 : 0) > mLogic.SLEEP_TIME) {
                continue;
            }

            if (mLogic.mPriceTable.getPrice(state, goods) >
                    mLogic.mPriceTable.getPrice(bestState, goods)) {
                bestState = state;
            }
        }
        return bestState;
    }

    public boolean isSuggestToSell() {
        if (!mLogic.canGoToMarket()) {
            return false;
        }

        for (Goods goods : Goods.values()) {
            if (mLogic.mInventory[goods.getValue()] == 0) {
                continue;
            }

            State bestStateToSell = findPlaceToSellGoods(goods);
            if (bestStateToSell == mLogic.mCurrentState) {
                mGoodsToDeal = goods;
                return true;
            }
        }

        return false;
    }

    public boolean isSuggestToSail() {
        if (mLogic.mCurrentHour >= mLogic.NIGHT_TIME) {
            return false;
        }

        for (Goods goods : Goods.values()) {
            if (mLogic.getInventory(goods) == 0) {
                continue;
            }

            State bestState = findPlaceToSellGoods(goods);
            if (bestState != mLogic.mCurrentState) {
                mGoodsToDeal = goods;
                mStateToSail = bestState;
                return true;
            }
        }

        mStateToSail = null;
        return true;
    }

    public boolean isSuggestToFixShip() {
        return mLogic.canFixShip();
    }

    public boolean isSuggestToBuy() {
        if (!mLogic.canGoToMarket()) {
            return false;
        }

        if (mLogic.calculateLoad() >= mLogic.mCapacity) {
            return false;
        }

        for (Goods goods : Goods.values()) {
            if (mLogic.mPriceTable.getPrice(mLogic.mCurrentState, goods) > mLogic.mCash) {
                // Cannot afford this goods.
                continue;
            }

            State bestState = findPlaceToSellGoods(goods);
            if (bestState != mLogic.mCurrentState) {
                mGoodsToDeal = goods;
                mStateToSail = bestState;
                return true;
            }
        }
        return false;
    }
}
