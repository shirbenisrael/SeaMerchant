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
        mPercentageOfValueForEnoughGuardShips = 90;         // TODO: Set this according relevant dangers.
        mMaxDepositWithEnoughGuardShips = (mMaxDeposit * mPercentageOfValueForEnoughGuardShips / 100);
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