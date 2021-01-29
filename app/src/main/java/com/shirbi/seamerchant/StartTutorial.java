package com.shirbi.seamerchant;

public class StartTutorial extends FrontEndGeneric {
    private FrontEnd mFrontEnd;
    private FrontEndMarket mFrontEndMarket;
    private FrontEndSail mFrontEndSail;
    TutorialStage mStage;

    public StartTutorial(MainActivity activity) {
        super(activity);
        mFrontEnd = mActivity.mFrontEnd;
        mFrontEndMarket = mActivity.mFrontEndMarket;
        mFrontEndSail = mActivity.mFrontEndSail;
        mStage = TutorialStage.STAGE_1;
    }

    public enum TutorialStage {
        STAGE_1, // buy wheat in israel
        STAGE_2, // ship to turkey
        STAGE_3; // sell wheat in turkey
    }

    public void endTutorial() {
        for (State state: State.values()) {
            mFrontEnd.setStateVisibility(state, true);
        }

        for (Goods goods: Goods.values()) {
            mFrontEnd.setGoodsVisibility(goods, true);
        }

        mFrontEnd.setCalculatorVisibility(true);
        mFrontEnd.setTutorialVisibility(true);

        mFrontEndMarket.showAllButton();
        mFrontEndSail.showAllButtons();
    }

    public void showStage1() { // buy wheat in israel
        mLogic.startNewGame();

        mFrontEnd.setStateVisibility(State.EGYPT, false);
        mFrontEnd.setStateVisibility(State.ISRAEL, true);
        mFrontEnd.setStateVisibility(State.TURKEY, true);
        mFrontEnd.setStateVisibility(State.CYPRUS, false);
        mFrontEnd.setStateVisibility(State.GREECE, false);

        mFrontEnd.setGoodsVisibility(Goods.WHEAT, true);
        mFrontEnd.setGoodsVisibility(Goods.OLIVES, false);
        mFrontEnd.setGoodsVisibility(Goods.COPPER, false);

        mFrontEnd.setCalculatorVisibility(false);
        mFrontEnd.setTutorialVisibility(false);

        mLogic.mPriceTable.setPrice(State.ISRAEL, Goods.WHEAT, 50);
        mLogic.mPriceTable.setPrice(State.TURKEY, Goods.WHEAT, 60);
        mFrontEnd.showPrices();

        mLogic.clearInventory();
        mFrontEnd.showInventory();

        String string1 = "החיטה בישראל זולה! כדאי לקנות!";
        String string2 = "לחץ על כפתור החיטה כדי לרכוש אותה";
        mFrontEnd.showTutorialStrings(string1, string2);
    }

    public void showStage2() { // ship to turkey
        String string1 = "מחיר החיטה בתורכיה גבוה! בוא נפליג לשם!";
        String string2 = "לחץ על הדגל של תורכיה כדי להפליג.";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkSail();
    }

    public void showStage3() { // sell wheat in turkey
        String string1 = "הגענו לתוכיה! בוא נמכור את החיטה ביוקר!";
        String string2 = "לחץ על כפתור החיטה כדי למכור אותה.";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();
    }

    public void onFlagClick(State destination) {
        switch (mStage) {
            case STAGE_1:
                return;
            case STAGE_2:
                if (destination == State.TURKEY) {
                    mLogic.initSail(destination);
                    mFrontEnd.showWindow(Window.SAIL_WINDOW);
                    mFrontEndSail.initSailRoute();
                    mFrontEndSail.showOnlyStartSail();
                }
        }
    }

    public void onMarketClick(Goods goods) {
        switch (mStage) {
            case STAGE_1:
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                break;
            case STAGE_3:
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                break;
        }
    }

    public void onMarketDealDoneClick() {
        switch (mStage) {
            case STAGE_1:
                if (mLogic.getInventory(Goods.WHEAT) == 100) {
                    mStage = TutorialStage.STAGE_2;
                    showStage2();
                }
                break;
        }
    }

    public void onSailEnd() {
        switch (mStage) {
            case STAGE_2:
                mStage = TutorialStage.STAGE_3;
                showStage3();
                break;
        }
    }
}
