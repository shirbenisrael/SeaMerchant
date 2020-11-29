package com.shirbi.seamerchant;

public class Sail {
    State mDestination;
    State mSource;
    int mLandingHour;
    int mValueOnShip; // inventory + cash
    int mGuardShipCost;
    int mMaxGuardShips;
    int mGuardShipCostPercent;
    static final int DEFAULT_GUARD_COST_PERCENT = 2;
    static final int MAX_GUARD_SHIPS = 5;
    static final int MIN_GUARD_SHIP_COST = 50;

    public Sail(Logic logic, State destination) {
        mDestination = destination;
        mSource = logic.mCurrentState;
        mLandingHour = logic.mCurrentHour + logic.getSailDuration(mDestination);
        mValueOnShip = logic.mCash;
        for (Goods goods : Goods.values()) {
            mValueOnShip += logic.mInventory[goods.getValue()] * logic.mPriceTable.getPrice(mSource, goods);
        }

        mGuardShipCostPercent = DEFAULT_GUARD_COST_PERCENT;
        mGuardShipCost = Math.max(MIN_GUARD_SHIP_COST, (mGuardShipCostPercent * mValueOnShip / 100));
        mMaxGuardShips = Math.min(MAX_GUARD_SHIPS, logic.mCash / mGuardShipCost);
    }
}
