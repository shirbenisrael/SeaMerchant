package com.shirbi.seamerchant;

public class FixShipDeal {
    int mMaxFix;
    int mCurrentFix;
    int mCash;

    public FixShipDeal(Logic logic) {
       mMaxFix = Math.min(logic.mDamage, logic.mCash);
       mCurrentFix = 0;
       mCash = logic.mCash;
    }

    public void setFix(int fix) {
        mCash += mCurrentFix;
        mCash -= fix;
        mCurrentFix = fix;
    }

    public void fixAsPossible() {
        setFix(mMaxFix);
    }
}
