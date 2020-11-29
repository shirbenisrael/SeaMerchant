package com.shirbi.seamerchant;

public class Sail {
    State mDestination;
    State mSource;
    int mValueOnShip; // inventory + cash
    int mGuardShipCost;
    int mMaxGuardShips;
    static final float DEFAULT_GUARD_COST = 0.02f;
    static final int MAX_GUARD_SHIPS = 5;
    static final int MIN_GUARD_SHIP_COST = 50;

    public Sail(Logic logic, State destination) {
        mDestination = destination;
        mSource = logic.mCurrentState;
        mValueOnShip = logic.mCash;
        for (Goods goods : Goods.values()) {
            mValueOnShip += logic.mInventory[goods.getValue()] * logic.mPriceTable.getPrice(mSource, goods);
        }
        mGuardShipCost = Math.max(MIN_GUARD_SHIP_COST, (int)(DEFAULT_GUARD_COST * mValueOnShip));
        mMaxGuardShips = Math.min(MAX_GUARD_SHIPS, logic.mCash / mGuardShipCost);
    }
}
