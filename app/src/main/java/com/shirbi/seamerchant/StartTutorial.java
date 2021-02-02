package com.shirbi.seamerchant;

public class StartTutorial extends FrontEndGeneric {
    private FrontEnd mFrontEnd;
    private FrontEndMarket mFrontEndMarket;
    private FrontEndSail mFrontEndSail;
    private FrontEndPirates mFrontEndPirates;
    private FrontEndBank mFrontEndBank;
    TutorialStage mStage;

    public StartTutorial(MainActivity activity) {
        super(activity);
        mFrontEnd = mActivity.mFrontEnd;
        mFrontEndMarket = mActivity.mFrontEndMarket;
        mFrontEndSail = mActivity.mFrontEndSail;
        mFrontEndPirates = mActivity.mFrontEndPirates;
        mFrontEndBank = mActivity.mFrontEndBank;
        mStage = TutorialStage.STAGE_1;
    }

    public enum TutorialStage {
        STAGE_1, // buy wheat in israel
        STAGE_2, // ship to turkey
        STAGE_3, // sell wheat in turkey
        STAGE_4, // buy olives in turkey
        STAGE_5, // ship to egypt with pirates and escape
        STAGE_6, // sell olives in egypt
        STAGE_7, // buy olives in egypt
        STAGE_8, // sail to cyprus
        STAGE_9, // sell wheat in cyprus
        STAGE_10, // go to bank
        STAGE_11, // go to sleep
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
        mFrontEnd.setBankVisibility(true);
        mFrontEnd.setFixVisibility(true);
        mFrontEnd.setSleepVisibility(true);
        mFrontEnd.setCapacityVisibility(true);

        mFrontEndMarket.showAllButton();
        mFrontEndSail.showAllButtons();
        mFrontEndPirates.showAllButtons();
        mFrontEndBank.showAllButtons();

        mFrontEnd.showState();
    }

    public void showStage1() { // buy wheat in israel
        mStage = TutorialStage.STAGE_1;
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
        mFrontEnd.setBankVisibility(false);
        mFrontEnd.setFixVisibility(false);
        mFrontEnd.setSleepVisibility(false);
        mFrontEnd.setCapacityVisibility(false);

        mLogic.mPriceTable.setPrice(State.ISRAEL, Goods.WHEAT, 50);
        mLogic.mPriceTable.setPrice(State.TURKEY, Goods.WHEAT, 60);
        mFrontEnd.showPrices();

        mLogic.clearInventory();
        mFrontEnd.showInventory();

        String string1 = "החיטה בישראל זולה! כדאי לקנות!";
        String string2 = "לחץ על כפתור החיטה כדי לרכוש אותה";
        mFrontEnd.showTutorialStrings(string1, string2);

        mFrontEnd.blinkMarket();
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

    public void showStage6() { // sell olives in egypt
        String string1 = "הגענו למצרים! בוא נמכור את הזיתים ביוקר!";
        String string2 = "לחץ על כפתור הזיתים כדי למכור אותם.";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();
    }

    public void showStage7() {
        String string1 = "החיטה במצרים זולה! כדאי לקנות!";
        String string2 = "לחץ על כפתור החיטה כדי לרכוש אותה.";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();

        mFrontEnd.setStateVisibility(State.CYPRUS, true);
        mLogic.mPriceTable.setPrice(State.CYPRUS, Goods.OLIVES, 650);
        mLogic.mPriceTable.setPrice(State.CYPRUS, Goods.WHEAT, 70);
        mFrontEnd.showPrices();
    }

    public void showStage8() {
        String string1 = "מחיר החיטה בקפריסין גבוה! בוא נפליג לשם!";
        String string2 = "לחץ על הדגל של קפריסין כדי להפליג.";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkSail();
    }

    public void showStage9() { // sell wheat in cyprus
        String string1 = "הגענו לקפריסין! בוא נמכור את החיטה ביוקר!";
        String string2 = "לחץ על כפתור החיטה כדי למכור אותה.";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();
    }

    public void showStage10() { // go to bank
        String string1 = "בוא נפקיד את הכסף בבנק לפני שנלך לישון.";
        String string2 = "לחץ על כפתור הבנק בשביל להפקיד את הכסף";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.setBankVisibility(true);
        mFrontEnd.blinkBank();
    }

    public void showStage11() { // go to sleep
        String string1 = "הגיע הזמן ללכת לישון.";
        String string2 = "לחץ על כפתור השינה כדי לנוח עד למחרת.";
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.setBankVisibility(false);
        mFrontEnd.setSleepVisibility(true);
        mFrontEnd.blinkSleep();
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
                    mFrontEnd.showAlertDialogMessage("לחץ על הוי הירוק כדי לצאת לדרך.", "הפלגה לתורכיה");
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
                mFrontEnd.showAlertDialogMessage("לחץ על הוי הירוק כדי לצאת לדרך.", "הפלגה למצרים");
                break;
            case STAGE_6:
                mFrontEnd.showAlertDialogMessage("בוא נמכור קודם את הזיתים שלנו.", "לא כדאי");
                mFrontEnd.blinkMarket();
                break;
            case STAGE_7:
                mFrontEnd.showAlertDialogMessage("בוא נקנה קודם חיטה.", "לא כדאי");
                mFrontEnd.blinkMarket();
                break;
            case STAGE_8:
                if (destination == State.ISRAEL) {
                    mFrontEnd.showAlertDialogMessage("מחיר החיטה ביוון גבוה יותר.", "לא כדאי");
                    break;
                }
                if (destination == State.TURKEY) {
                    mFrontEnd.showAlertDialogMessage("מאוחר מדי להפלגה כה ארוכה.", "לא כדאי");
                    break;
                }
                mLogic.initSail(destination);
                mFrontEnd.showWindow(Window.SAIL_WINDOW);
                mFrontEndSail.initSailRoute();
                mFrontEndSail.showAllButtons();
                mFrontEnd.showAlertDialogMessage("לחץ על כפתור 5 כדי לשכור ספינות משמר ואז על הוי הירוק כדאי לצאת לדרך.", "הפלגה לקפריסין");
                break;
            case STAGE_9:
                mFrontEnd.showAlertDialogMessage("מאוחר מדי בשביל להפליג.", "לא כדאי");
                mFrontEnd.blinkMarket();
                break;
            case STAGE_10:
                mFrontEnd.showAlertDialogMessage("מאוחר מדי בשביל להפליג. נלך לבנק.", "לא כדאי");
                mFrontEnd.blinkBank();
                break;
            case STAGE_11:
                mFrontEnd.showAlertDialogMessage("מאוחר מדי בשביל להפליג. נלך לישון.", "לא כדאי");
                mFrontEnd.blinkSleep();
        }
    }

    public void onMarketClick(Goods goods) {
        switch (mStage) {
            case STAGE_1:
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                mFrontEnd.showAlertDialogMessage("לחץ על -קנה מקסימום- ואז על כפתור וי ירוק.", "קניית חיטה");
                break;
            case STAGE_2:
                mFrontEnd.showAlertDialogMessage("קנינו מספיק. עכשיו הזמן להפליג.", "לא כדאי");
                mFrontEnd.blinkSail();
                break;
            case STAGE_3:
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showAlertDialogMessage("לחץ על -מכור הכל- ואז על כפתור וי ירוק.", "מכירת חיטה");
                break;
            case STAGE_4:
                if (goods == Goods.WHEAT) {
                    mFrontEnd.showAlertDialogMessage("אין טעם לקנות כאן חיטה. היא יקרה מדי.", "לא כדאי");
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                mFrontEnd.showAlertDialogMessage("לחץ על -קנה מקסימום- ואז על כפתור וי ירוק.", "קניית זיתים");
                break;
            case STAGE_5:
                mFrontEnd.showAlertDialogMessage("קנינו מספיק. עכשיו הזמן להפליג.", "לא כדאי");
                break;
            case STAGE_6:
                if (goods == Goods.WHEAT) {
                    mFrontEnd.showAlertDialogMessage("בוא נמכור קודם כל את הזיתים.", "לא כדאי");
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showAlertDialogMessage("לחץ על -מכור הכל- ואז על כפתור וי ירוק.", "מכירת זיתים");
                break;
            case STAGE_7:
                if (goods == Goods.OLIVES) {
                    mFrontEnd.showAlertDialogMessage("בוא נקנה חיטה.", "לא כדאי");
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyFillCapacity();
                mFrontEnd.showAlertDialogMessage("לחץ על -קיבולת מלאה- כדי למלא את הספינה בהתאם לקיבולת ואז על כפתור וי ירוק.", "קנית חיטה");
                break;
            case STAGE_8:
                mFrontEnd.showAlertDialogMessage("קנינו מספיק. עכשיו הזמן להפליג.", "לא כדאי");
                break;
            case STAGE_9:
                if (goods == Goods.OLIVES) {
                    mFrontEnd.showAlertDialogMessage("בוא נמכור את החיטה.", "לא כדאי");
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showAlertDialogMessage("לחץ על -מכור הכל- ואז על כפתור וי ירוק.", "מכירת חיטה");
                break;
            case STAGE_10:
                mFrontEnd.showAlertDialogMessage("מאוחר מדי לקניות. נלך לבנק.", "לא כדאי");
                mFrontEnd.blinkBank();
            case STAGE_11:
                mFrontEnd.showAlertDialogMessage("מאוחר מדי לקניות. נלך לישון.", "לא כדאי");
                mFrontEnd.blinkSleep();
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
            case STAGE_6:
                if (mLogic.getInventory(Goods.OLIVES) == 0) {
                    mStage = TutorialStage.STAGE_7;
                    showStage7();
                }
                break;
            case STAGE_7:
                if (mLogic.getInventory(Goods.WHEAT) > 0) {
                    mStage = TutorialStage.STAGE_8;
                    showStage8();
                }
                break;
            case STAGE_9:
                if (mLogic.getInventory(Goods.WHEAT) == 0) {
                    mStage = TutorialStage.STAGE_10;
                    showStage10();
                }
                break;
        }
    }

    public void onBankDealDoneClick() {
        switch (mStage) {
            case STAGE_10:
                if (mLogic.mCash == 0) {
                    mStage = TutorialStage.STAGE_11;
                    showStage11();
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
            case STAGE_5:
                mStage = TutorialStage.STAGE_6;
                showStage6();
                break;
            case STAGE_8:
                mStage = TutorialStage.STAGE_9;
                showStage9();
                break;
        }
    }

    public void onMiddleSail() {
        switch (mStage) {
            case STAGE_5:
                mFrontEndSail.pauseSail();
                mActivity.showPirates();
                mFrontEndPirates.showOnlyEscape();
                break;
            case STAGE_8:
                mFrontEndSail.pauseSail();
                mActivity.showPirates();
                mFrontEndPirates.showOnlyAttack();
                break;
        }
    }

    public void onBankClick() {
        switch (mStage) {
            case STAGE_10:
                mLogic.initBankDeal();
                mFrontEnd.showWindow(Window.BANK_WINDOW);
                mFrontEndBank.onBankClick();
                mFrontEndBank.showOnlyDepositAll();
                mFrontEnd.showAlertDialogMessage("להפקדה - לחץ על -הפקד הכל- ואז על הוי הירוק.", "הפקדה בבנק");
                break;
        }
    }

    public boolean checkIfCanSail() {
        switch (mStage) {
            case STAGE_8:
                if (mLogic.mSail.mSelectedNumGuardShips < 5) {
                    mFrontEnd.showAlertDialogMessage("הים שורץ שודדים. בוא נשכור 5 ספינות משמר. לחץ על כפתור ה-5.", "לא כדאי");
                    return false;
                }
        }
        return  true;
    }

    public void attackPirates() {
        mLogic.mSail.mPiratesDamage = 0;
        mLogic.mSail.mBattleResult = Sail.BattleResult.WIN_AND_TREASURE;
        mLogic.mSail.mPiratesTreasure = 1000;
        mLogic.mCash += mLogic.mSail.mPiratesTreasure;
        mFrontEndPirates.showWinPiratesMessage();
        mFrontEnd.showWindow(Window.PIRATES_ATTACK_WINDOW);
    }
}
