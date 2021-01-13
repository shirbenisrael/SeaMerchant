package com.shirbi.seamerchant;

public class BankDeal {
    int mDeposit;
    int mCash;
    int mMaxDeposit;
    int mMaxDepositWithEnoughGuardShips;
    int mPercentageOfValueForEnoughGuardShips;

    public BankDeal(Logic logic) {
        mDeposit = logic.mBankDeposit;
        mCash = logic.mCash;

        mMaxDeposit = mDeposit + mCash;

        int inventoryValue = logic.calculateInventoryValue();
        float guardsForInventoryPart = 0.1f;
        float cashForGuardsForInventoryAndCash = inventoryValue / (1/guardsForInventoryPart - 1);

        int minCashForGuards = Sail.MIN_GUARD_SHIP_COST * Sail.MAX_GUARD_SHIPS;
        if (logic.hasMedal(Medal.ALWAYS_FIGHTER)) {
            minCashForGuards -= Sail.MIN_GUARD_SHIP_COST;
        }
        cashForGuardsForInventoryAndCash = Math.max(cashForGuardsForInventoryAndCash, minCashForGuards);

        mMaxDepositWithEnoughGuardShips = (int)(mMaxDeposit - cashForGuardsForInventoryAndCash);
    }

    public void setDeposit(int units) {
        if (units > mDeposit) {
            addDeposit(units - mDeposit);
        } else {
            removeDeposit(mDeposit - units);
        }
    }

    public void addDeposit(int units) {
        if (mCash >= units) {
            mDeposit += units;
            mCash -= units;
        } else {
            depositAll();
        }
    }

    public void removeDeposit(int units) {
        if (mDeposit >= units) {
            mDeposit -= units;
            mCash += units;
        } else {
            drawAll();
        }
    }

    public void depositAll() {
        addDeposit(mCash);
    }

    public void drawAll() {
        removeDeposit(mDeposit);
    }

    public void leaveCashForGuards() {
        setDeposit(mMaxDepositWithEnoughGuardShips);
    }
}