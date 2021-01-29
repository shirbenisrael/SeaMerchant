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
        STAGE_3, // sell wheat in turkey
        STAGE_4, // buy olives in turkey
        STAGE_5; // ship to egypt
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

    public void showStage4() { // buy olives in turkey.
        String string1 = "הזיתים בתורכיה זולים! כדאי לקנות!";
        String string2 = "לחץ על כפתור הזיתים כדי לרכוש אותם.";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();

        mFrontEnd.setStateVisibility(State.EGYPT, true);

        mFrontEnd.setGoodsVisibility(Goods.WHEAT, true);
        mFrontEnd.setGoodsVisibility(Goods.OLIVES, true);

        mLogic.mPriceTable.setPrice(State.TURKEY, Goods.OLIVES, 400);
        mLogic.mPriceTable.setPrice(State.EGYPT, Goods.OLIVES, 650);
        mLogic.mPriceTable.setPrice(State.ISRAEL, Goods.OLIVES, 350);
        mLogic.mPriceTable.setPrice(State.EGYPT, Goods.WHEAT, 35);
        mFrontEnd.showPrices();
    }

    public void showStage5() { // ship to egypt
        String string1 = "מחיר הזיתים במצרים גבוה! בוא נפליג לשם!";
        String string2 = "לחץ על הדגל של מצרים כדי להפליג.";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkSail();
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
                break;
            case STAGE_3:
                mFrontEnd.showAlertDialogMessage("בוא נמכור קודם את החיטה שלנו.", "לא כדאי");
                mFrontEnd.blinkMarket();
                break;
            case STAGE_4:
                mFrontEnd.showAlertDialogMessage("בוא נקנה קודם זיתים.", "לא כדאי");
                mFrontEnd.blinkMarket();
                break;
            case STAGE_5:
                if (destination == State.ISRAEL) {
                    mFrontEnd.showAlertDialogMessage("מחיר הזיתים בישראל נמוך מדי.", "לא כדאי");
                    break;
                }
                mLogic.initSail(destination);
                mFrontEnd.showWindow(Window.SAIL_WINDOW);
                mFrontEndSail.initSailRoute();
                mFrontEndSail.showOnlyStartSail();
                break;
        }
    }

    public void onMarketClick(Goods goods) {
        switch (mStage) {
            case STAGE_1:
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                break;
            case STAGE_2:
                mFrontEnd.showAlertDialogMessage("קנינו מספיק. עכשיו הזמן להפליג.", "לא כדאי");
                mFrontEnd.blinkSail();
                break;
            case STAGE_3:
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                break;
            case STAGE_4:
                if (goods == Goods.WHEAT) {
                    mFrontEnd.showAlertDialogMessage("אין טעם לקנות כאן חיטה. היא יקרה מדי.", "לא כדאי");
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                break;
            case STAGE_5:
                mFrontEnd.showAlertDialogMessage("קנינו מספיק. עכשיו הזמן להפליג.", "לא כדאי");
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
            case STAGE_3:
                if (mLogic.getInventory(Goods.WHEAT) == 0) {
                    mStage = TutorialStage.STAGE_4;
                    showStage4();
                }
                break;
            case STAGE_4:
                if (mLogic.getInventory(Goods.OLIVES) > 0) {
                    mStage = TutorialStage.STAGE_5;
                    showStage5();
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
