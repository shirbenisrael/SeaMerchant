package com.shirbi.seamerchant;

public class FixShipDeal {
    long mMaxFix;
    long mCurrentFix;
    long mCash;

    public FixShipDeal(Logic logic) {
       mMaxFix = Math.min(logic.mDamage, logic.mCash);
       mCurrentFix = 0;
       mCash = logic.mCash;
    }

    public void setFix(long fix) {
        mCash += mCurrentFix;
        mCash -= fix;
        mCurrentFix = fix;
    }

    public void fixAsPossible() {
        setFix(mMaxFix);
    }
}
