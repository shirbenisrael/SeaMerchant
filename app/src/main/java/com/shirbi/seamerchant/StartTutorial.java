package com.shirbi.seamerchant;

public class StartTutorial extends FrontEndGeneric {
    private FrontEnd mFrontEnd;
    private FrontEndMarket mFrontEndMarket;
    private FrontEndSail mFrontEndSail;
    private FrontEndPirates mFrontEndPirates;
    private FrontEndBank mFrontEndBank;
    private FrontEndFixShip mFrontEndFixShip;
    TutorialStage mStage;

    public StartTutorial(MainActivity activity) {
        super(activity);
        mFrontEnd = mActivity.mFrontEnd;
        mFrontEndMarket = mActivity.mFrontEndMarket;
        mFrontEndSail = mActivity.mFrontEndSail;
        mFrontEndPirates = mActivity.mFrontEndPirates;
        mFrontEndBank = mActivity.mFrontEndBank;
        mFrontEndFixShip = mActivity.mFrontEndFixShip;

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
        STAGE_10, // go to bank to deposit
        STAGE_11, // go to sleep
        STAGE_12, // go to bank to draw cash
        STAGE_13, // buy copper
        STAGE_14, // sail to greece
        STAGE_15, // fix ship
        STAGE_16, // sell copper
        STAGE_17, // buy wheat in greece
        STAGE_18, // sail to cyprus
        STAGE_19, // sell wheat in cyprus
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
        mFrontEnd.resetMarketStateText();

        mFrontEndMarket.showAllButton();
        mFrontEndSail.showAllButtons();
        mFrontEndPirates.showAllButtons();
        mFrontEndBank.showAllButtons();

        mFrontEnd.showState();
    }

    public void showStage1() { // buy wheat in israel
        mStage = TutorialStage.STAGE_1;
        mLogic.startNewGame();
        mFrontEnd.showState();

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

        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS, getGoodsString(Goods.WHEAT), getStateString(State.ISRAEL));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY, getGoodsString(Goods.WHEAT));
        mFrontEnd.showTutorialStrings(string1, string2);

        mFrontEnd.blinkMarket();
    }

    public void showStage2() { // ship to turkey
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE, getGoodsString(Goods.WHEAT), getStateString(State.TURKEY));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE, getStateString(State.TURKEY));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkSail();
    }

    public void showStage3() { // sell wheat in turkey
        String string1 = mActivity.getString(R.string.REACH_STATE_AND_SELL_GOODS, getStateString(State.TURKEY), getGoodsString(Goods.WHEAT));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL, getGoodsString(Goods.WHEAT));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();
    }

    public void showStage4() { // buy olives in turkey.
        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS, getGoodsString(Goods.OLIVES), getStateString(State.TURKEY));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY, getGoodsString(Goods.OLIVES));
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
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE, getGoodsString(Goods.OLIVES), getStateString(State.EGYPT));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE, getStateString(State.EGYPT));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkSail();
    }

    public void showStage6() { // sell olives in egypt
        String string1 = mActivity.getString(R.string.REACH_STATE_AND_SELL_GOODS, getStateString(State.EGYPT), getGoodsString(Goods.OLIVES));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL, getGoodsString(Goods.OLIVES));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();
    }

    public void showStage7() {
        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS, getGoodsString(Goods.WHEAT), getStateString(State.EGYPT));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY, getGoodsString(Goods.WHEAT));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();

        mFrontEnd.setStateVisibility(State.CYPRUS, true);
        mLogic.mPriceTable.setPrice(State.CYPRUS, Goods.OLIVES, 650);
        mLogic.mPriceTable.setPrice(State.CYPRUS, Goods.WHEAT, 70);
        mFrontEnd.showPrices();
    }

    public void showStage8() {
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE, getGoodsString(Goods.WHEAT), getStateString(State.CYPRUS));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE, getStateString(State.CYPRUS));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkSail();
    }

    public void showStage9() { // sell wheat in cyprus
        String string1 = mActivity.getString(R.string.REACH_STATE_AND_SELL_GOODS, getStateString(State.CYPRUS), getGoodsString(Goods.WHEAT));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL, getGoodsString(Goods.WHEAT));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();
    }

    public void showStage10() { // go to bank
        String string1 = getString(R.string.DEPOSIT_IN_BANK_BEFORE_SLEEP);
        String string2 = getString(R.string.PRESS_BANK_BUTTON_TO_DEPOSIT);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.setBankVisibility(true);
        mFrontEnd.blinkBank();
    }

    public void showStage11() { // go to sleep
        String string1 = getString(R.string.TIME_TO_GO_TO_SLEEP);
        String string2 = getString(R.string.PRESS_SLEEP_BUTTON_TO_REST);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.setBankVisibility(false);
        mFrontEnd.setSleepVisibility(true);
        mFrontEnd.blinkSleep();
    }

    public void showStage12() { // draw cash from bank
        mLogic.mPriceTable.setPrice(State.ISRAEL, Goods.WHEAT, 35);
        mLogic.mPriceTable.setPrice(State.TURKEY, Goods.WHEAT, 35);
        mLogic.mPriceTable.setPrice(State.EGYPT, Goods.WHEAT, 35);
        mLogic.mPriceTable.setPrice(State.CYPRUS, Goods.WHEAT, 70);
        mLogic.mPriceTable.setPrice(State.GREECE, Goods.WHEAT, 20);

        mLogic.mPriceTable.setPrice(State.ISRAEL, Goods.OLIVES, 350);
        mLogic.mPriceTable.setPrice(State.TURKEY, Goods.OLIVES, 350);
        mLogic.mPriceTable.setPrice(State.EGYPT, Goods.OLIVES, 350);
        mLogic.mPriceTable.setPrice(State.CYPRUS, Goods.OLIVES, 600);
        mLogic.mPriceTable.setPrice(State.GREECE, Goods.OLIVES, 600);

        mLogic.mPriceTable.setPrice(State.ISRAEL, Goods.COPPER, 2500);
        mLogic.mPriceTable.setPrice(State.TURKEY, Goods.COPPER, 2500);
        mLogic.mPriceTable.setPrice(State.EGYPT, Goods.COPPER, 2500);
        mLogic.mPriceTable.setPrice(State.CYPRUS, Goods.COPPER, 2500);
        mLogic.mPriceTable.setPrice(State.GREECE, Goods.COPPER, 4500);

        mFrontEnd.setGoodsVisibility(Goods.COPPER, true);
        mFrontEnd.setStateVisibility(State.GREECE, true);
        mFrontEnd.showState();

        String string1 = getString(R.string.DRAW_MONEY_FROM_BANK);
        String string2 = getString(R.string.PRESS_BANK_BUTTON_TO_DRAW);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.setBankVisibility(true);
        mFrontEnd.setSleepVisibility(false);
        mFrontEnd.blinkBank();
    }

    public void showStage13() { // buy cooper
        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS, getGoodsString(Goods.COPPER), getStateString(State.CYPRUS));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY, getGoodsString(Goods.COPPER));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();
    }

    public void showStage14() { // sail to greece
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE, getGoodsString(Goods.COPPER), getStateString(State.GREECE));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE, getStateString(State.GREECE));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkSail();
    }

    public void showStage15() { // fix ship
        String string1 = getString(R.string.WE_SHOUD_FIX_DAMAGED_SHIP);
        String string2 = getString(R.string.PRESS_ON_THE_FIX_BUTTON);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.setFixVisibility(true);
        mFrontEnd.blinkFixShip();
    }

    public void showStage16() { // sell copper in greece
        String string1 = mActivity.getString(R.string.SELL_GOODS, getGoodsString(Goods.COPPER));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL, getGoodsString(Goods.COPPER));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();
    }

    public void showStage17() { // buy wheat in greece
        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS, getGoodsString(Goods.WHEAT), getStateString(State.GREECE));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY, getGoodsString(Goods.WHEAT));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket();
    }

    public void showStage18() { // sail to cyprus
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE, getGoodsString(Goods.WHEAT), getStateString(State.CYPRUS));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE, getStateString(State.CYPRUS));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkSail();
    }

    public void showStage19() { // sell wheat in cyprus
        String string1 = mActivity.getString(R.string.REACH_STATE_AND_SELL_MOST_GOODS, getStateString(State.CYPRUS), getGoodsString(Goods.WHEAT));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL, getGoodsString(Goods.WHEAT));
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
                    mFrontEnd.showAlertDialogMessage(getString(R.string.PRESS_ON_V_TO_SAIL),
                            mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.TURKEY)));
                }
                break;
            case STAGE_3:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.WE_SHOULD_SELL_FIRST, getGoodsString(Goods.WHEAT)), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket();
                break;
            case STAGE_4:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.WE_SHOULD_BUY_FIRST, getGoodsString(Goods.OLIVES)), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket();
                break;
            case STAGE_5:
                if (destination == State.ISRAEL) {
                    mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.GOODS_PRICE_IS_TO_LOW_IN_STATE,
                            getGoodsString(Goods.OLIVES), getStateString(State.ISRAEL)), getString(R.string.WE_SHOULD_NOT));
                    break;
                }
                mLogic.initSail(destination);
                mFrontEnd.showWindow(Window.SAIL_WINDOW);
                mFrontEndSail.initSailRoute();
                mFrontEndSail.showOnlyStartSail();
                mFrontEnd.showAlertDialogMessage(getString(R.string.PRESS_ON_V_TO_SAIL), mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.EGYPT)));
                break;
            case STAGE_6:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.WE_SHOULD_SELL_FIRST, getGoodsString(Goods.OLIVES)),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket();
                break;
            case STAGE_7:
            case STAGE_17:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.WE_SHOULD_BUY_FIRST, getGoodsString(Goods.WHEAT)),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket();
                break;
            case STAGE_8:
                if (destination == State.ISRAEL) {
                    mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.GOODS_PRICE_IS_TO_HIGHER_IN_STATE, getGoodsString(Goods.WHEAT),
                            getStateString(State.CYPRUS)), getString(R.string.WE_SHOULD_NOT));
                    break;
                }
                if (destination == State.TURKEY) {
                    mFrontEnd.showAlertDialogMessage(getString(R.string.TO_LATE_FOR_LONG_SAIL), getString(R.string.WE_SHOULD_NOT));
                    break;
                }
                mLogic.initSail(destination);
                mFrontEnd.showWindow(Window.SAIL_WINDOW);
                mFrontEndSail.initSailRoute();
                mFrontEndSail.showAllButtons();
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.GET_5_GUARDS), mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.CYPRUS)));
                break;
            case STAGE_9:
                mFrontEnd.showAlertDialogMessage(getString(R.string.TO_LATE_FOR_SAIL), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket();
                break;
            case STAGE_10:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.TO_LATE_FOR_SAIL_GO_TO_BANK), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkBank();
                break;
            case STAGE_11:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.TO_LATE_FOR_SAIL_GO_TO_SLEEP), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkSleep();
                break;
            case STAGE_12:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.DRAW_CASH_FIRST), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkBank();
                break;
            case STAGE_13:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.WE_SHOULD_BUY_FIRST, getGoodsString(Goods.COPPER)),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket();
                break;
            case STAGE_14:
                if (destination == State.GREECE) {
                    mLogic.initSail(destination);
                    mFrontEnd.showWindow(Window.SAIL_WINDOW);
                    mFrontEndSail.initSailRoute();
                    mFrontEnd.showAlertDialogMessage(getString(R.string.GET_5_GUARDS), mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.GREECE)));
                } else {
                    mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.GOODS_PRICE_IS_TO_HIGHER_IN_STATE, getGoodsString(Goods.COPPER),
                            getStateString(State.GREECE)), getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkSail();
                }
                break;
            case STAGE_15:
                mFrontEnd.showAlertDialogMessage(getString(R.string.FIX_SHIP_BEFORE_SAIL), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkFixShip();
                break;
            case STAGE_16:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.WE_SHOULD_SELL_FIRST, getGoodsString(Goods.COPPER)), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket();
                break;
            case STAGE_18:
                mLogic.initSail(destination);
                mFrontEnd.showWindow(Window.SAIL_WINDOW);
                mFrontEndSail.initSailRoute();
                mFrontEnd.showAlertDialogMessage(getString(R.string.PRESS_ON_V_TO_SAIL), mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.CYPRUS)));
                break;
            case STAGE_19:
                mFrontEnd.showAlertDialogMessage(mActivity.getString(R.string.WE_SHOULD_SELL_FIRST, getGoodsString(Goods.WHEAT)),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket();
                break;
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
            case STAGE_18:
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
            case STAGE_14:
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
                break;
            case STAGE_11:
                mFrontEnd.showAlertDialogMessage("מאוחר מדי לקניות. נלך לישון.", "לא כדאי");
                mFrontEnd.blinkSleep();
                break;
            case STAGE_12:
                mFrontEnd.showAlertDialogMessage("בוא נמשוך קודם את הכסף מהבנק.", "לא כדאי");
                mFrontEnd.blinkBank();
                break;
            case STAGE_13:
                if (goods != Goods.COPPER) {
                    mFrontEnd.showAlertDialogMessage("אין טעם. עדיף לקנות כאן נחושת.", "לא כדאי");
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                mFrontEnd.showAlertDialogMessage("לחץ על -קנה מקסימום- ואז על כפתור וי ירוק.", "קניית נחושת");
                break;
            case STAGE_15:
                mFrontEnd.showAlertDialogMessage("בוא נתקן את הספינה קודם.", "לא כדאי");
                mFrontEnd.blinkFixShip();
                break;
            case STAGE_16:
                if (goods != Goods.COPPER) {
                    mFrontEnd.showAlertDialogMessage("בוא נמכור קודם את הנחושת.", "לא כדאי");
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showAlertDialogMessage("לחץ על -מכור הכל- ואז על כפתור וי ירוק.", "מכירת נחושת");
                break;
            case STAGE_17:
                if (goods != Goods.WHEAT) {
                    mFrontEnd.showAlertDialogMessage("אין טעם. עדיף לקנות כאן חיטה.", "לא כדאי");
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                mFrontEnd.showAlertDialogMessage("לחץ על -קנה מקסימום- ואז על כפתור וי ירוק.", "קניית חיטה");
                break;
            case STAGE_19:
                if (goods != Goods.WHEAT) {
                    mFrontEnd.showAlertDialogMessage("בוא נמכור קודם את החיטה.", "לא כדאי");
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showAlertDialogMessage("לחץ על -מכור הכל- ואז על כפתור וי ירוק.", "מכירת חיטה");
                break;
        }
    }

    public void onMarketDealDoneClick() {
        switch (mStage) {
            case STAGE_1:
                if (mLogic.getInventory(Goods.WHEAT) > 0) {
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
            case STAGE_13:
                if (mLogic.getInventory(Goods.COPPER) != 0) {
                    mStage = TutorialStage.STAGE_14;
                    showStage14();
                }
                break;
            case STAGE_16:
                if (mLogic.getInventory(Goods.COPPER) == 0) {
                    mStage = TutorialStage.STAGE_17;
                    showStage17();
                }
                break;
            case STAGE_17:
                if (mLogic.getInventory(Goods.WHEAT) > 0 ) {
                    mStage = TutorialStage.STAGE_18;
                    showStage18();
                }
                break;
            case STAGE_19:
                if (mLogic.getInventory(Goods.WHEAT) == 0 ) {
                    mFrontEnd.startNewGameAfterTutorial();
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
            case STAGE_12:
                if (mLogic.mCash != 0) {
                    mStage = TutorialStage.STAGE_13;
                    showStage13();
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
            case STAGE_14:
                mStage = TutorialStage.STAGE_15;
                showStage15();
                break;
            case STAGE_18:
                mStage = TutorialStage.STAGE_19;
                showStage19();
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
            case STAGE_14:
                mFrontEndSail.pauseSail();
                mActivity.showPirates();
                mFrontEndPirates.showOnlyAttack();
                break;
            case STAGE_18:
                mFrontEndSail.pauseSail();
                mActivity.showSink();
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
            case STAGE_12:
                mLogic.initBankDeal();
                mFrontEnd.showWindow(Window.BANK_WINDOW);
                mFrontEndBank.onBankClick();
                mFrontEndBank.showOnlyDrawAll();
                mFrontEnd.showAlertDialogMessage("למשיכת כספנו כולל הריבית הלילית - לחץ על -משוך הכל- ואז על הוי הירוק.", "משיכה מהבנק");
                break;
            case STAGE_13:
                mFrontEnd.showAlertDialogMessage("בוא נקנה חנושת.", "לא כדאי");
                mFrontEnd.blinkMarket();
                break;
            case STAGE_14:
                mFrontEnd.showAlertDialogMessage("בוא נפליג ליוון.", "לא כדאי");
                mFrontEnd.blinkSail();
                break;
            case STAGE_15:
                mFrontEnd.showAlertDialogMessage("בוא נתקן את הספינה קודם.", "לא כדאי");
                mFrontEnd.blinkFixShip();
                break;
            case STAGE_16:
                mFrontEnd.showAlertDialogMessage("בוא נמכור את החנושת.", "לא כדאי");
                mFrontEnd.blinkMarket();
                break;
            case STAGE_17:
                mFrontEnd.showAlertDialogMessage("בוא נקנה חיטה.", "לא כדאי");
                mFrontEnd.blinkMarket();
                break;
            case STAGE_18:
                mFrontEnd.showAlertDialogMessage("בוא נפליג לקפריסין.", "לא כדאי");
                mFrontEnd.blinkSail();
                break;
            case STAGE_19:
                mFrontEnd.showAlertDialogMessage("בוא נמכור את החיטה.", "לא כדאי");
                mFrontEnd.blinkMarket();
                break;
        }
    }

    public boolean checkIfCanSail() {
        switch (mStage) {
            case STAGE_8:
            case STAGE_14:
                if (mLogic.mSail.mSelectedNumGuardShips < 5) {
                    mFrontEnd.showAlertDialogMessage("הים שורץ שודדים. בוא נשכור 5 ספינות משמר. לחץ על כפתור ה-5.", "לא כדאי");
                    return false;
                }
        }
        return  true;
    }

    public void createSink() {
        mLogic.mSail.mSailEndedPeacefully = false;
        mLogic.mSail.mSinkGood = Goods.WHEAT;
        mLogic.mSail.mSinkGoodsUnitsLost = 100;
        mLogic.removeGoodsFromInventory(mLogic.mSail.mSinkGood, mLogic.mSail.mSinkGoodsUnitsLost);
    }

    public void attackPirates() {
        switch (mStage) {
            case STAGE_8:
                mLogic.mSail.mPiratesDamage = 0;
                mLogic.mSail.mBattleResult = Sail.BattleResult.WIN_AND_TREASURE;
                mLogic.mSail.mPiratesTreasure = 1000;
                mLogic.mCash += mLogic.mSail.mPiratesTreasure;
                mFrontEndPirates.showWinPiratesMessage();
                mFrontEnd.showWindow(Window.PIRATES_ATTACK_WINDOW);
                break;
            case STAGE_14:
                mLogic.mSail.mPiratesDamage = 5000;
                mLogic.mSail.mBattleResult = Sail.BattleResult.WIN_AND_TREASURE;
                mLogic.mSail.mPiratesTreasure = 10000;
                mLogic.mCash += mLogic.mSail.mPiratesTreasure;
                mLogic.mDamage += mLogic.mSail.mPiratesDamage;
                mFrontEndPirates.showWinPiratesMessage();
                mFrontEnd.showWindow(Window.PIRATES_ATTACK_WINDOW);
                break;
        }
    }

    public void startNewDay() {
        mLogic.mWeather = Weather.GOOD_SAILING;
    }

    public void startNewDayAfterWeather() {
        mStage = TutorialStage.STAGE_12;
        showStage12();
    }

    public void onFixButtonClick() {
        mLogic.initFixShipDeal();
        mFrontEndFixShip.onFixShipClick();
        mFrontEnd.showWindow(Window.FIX_SHIP_WINDOW);
        mFrontEnd.showAlertDialogMessage("לחץ על -תקן ככל האפשר- ואז על כפתור וי ירוק.", "תיקון הספינה");
    }

    public void fixDone() {
        if (mLogic.mDamage == 0) {
            mStage = TutorialStage.STAGE_16;
            showStage16();
        }
    }
}
