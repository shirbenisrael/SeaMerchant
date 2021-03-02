package com.shirbi.seamerchant;

public class BankDeal {
    long mDeposit;
    long mCash;
    long mMaxDeposit;
    long mMaxDepositWithEnoughGuardShips;

    public BankDeal(Logic logic) {
        mDeposit = logic.mBankDeposit;
        mCash = logic.mCash;

        mMaxDeposit = mDeposit + mCash;

        long inventoryValue = logic.calculateInventoryValue();
        float guardsForInventoryPart = 0.1f;

        if (logic.mCurrentState == logic.mWeatherState) {
            // if we know we are in bad weather, then we know guards will cost more.
            guardsForInventoryPart *= Sail.getWeatherGuardCostMultiplyFromHere(logic.mWeather);
        } else if(logic.mCurrentState == State.GREECE && logic.mWeatherState == State.CYPRUS) {
            // if we know we are going to bad weather, then we know guards will cost more.
            guardsForInventoryPart *= Sail.getWeatherGuardCostMultiplyToThere(logic.mWeather);
        }

        if (!logic.isStillDayTimeAfterBankOperation()) {
            guardsForInventoryPart *= Sail.NIGHT_SAIL_GUARD_COST_PERCENT_MULTIPLY;
        }

        float cashForGuardsForInventoryAndCash = inventoryValue / (1/guardsForInventoryPart - 1);

        long minCashForGuards = Sail.MIN_GUARD_SHIP_COST * Sail.MAX_GUARD_SHIPS;
        if (logic.hasMedal(Medal.ALWAYS_FIGHTER)) {
            minCashForGuards -= Sail.MIN_GUARD_SHIP_COST;
        }
        cashForGuardsForInventoryAndCash = Math.max(cashForGuardsForInventoryAndCash, minCashForGuards);
        long CashForGuard = (long) Math.ceil(cashForGuardsForInventoryAndCash);

        mMaxDepositWithEnoughGuardShips = (long)(mMaxDeposit - CashForGuard);
    }

    public void setDeposit(long units) {
        if (units > mDeposit) {
            addDeposit(units - mDeposit);
        } else {
            removeDeposit(mDeposit - units);
        }
    }

    public void addDeposit(long units) {
        if (mCash >= units) {
            mDeposit += units;
            mCash -= units;
        } else {
            depositAll();
        }
    }

    public void removeDeposit(long units) {
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