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
        mFrontEnd.removeHighLightPrices();

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

        mFrontEnd.setRedTutorialMessages(false);

        mFrontEndMarket.showAllButton();
        mFrontEndSail.showAllButtons();
        mFrontEndPirates.showAllButtons();
        mFrontEndBank.showAllButtons();

        mFrontEnd.showState();
    }

    public void showStage1() { // buy wheat in israel
        mStage = TutorialStage.STAGE_1;
        mLogic.startNewGame();

        mLogic.mCapacity = Logic.START_CAPACITY; // Ignore medal
        mLogic.mCash = Logic.START_GAME_CASH; // Ignore medal
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

        mFrontEnd.setRedTutorialMessages(true);

        mLogic.mPriceTable.setPrice(State.ISRAEL, Goods.WHEAT, 50);
        mLogic.mPriceTable.setPrice(State.TURKEY, Goods.WHEAT, 60);
        mFrontEnd.showPrices();

        mLogic.clearInventory();
        mFrontEnd.showInventory();

        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS, getGoodsString(Goods.WHEAT), getStateString(State.ISRAEL));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY, getGoodsString(Goods.WHEAT));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.ISRAEL, Goods.WHEAT, true);
        mFrontEnd.blinkMarket(Goods.WHEAT);
    }

    public void showStage2() { // ship to turkey
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE, getGoodsString(Goods.WHEAT), getStateString(State.TURKEY));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE, getStateString(State.TURKEY));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.TURKEY, Goods.WHEAT, true);
        mFrontEnd.blinkSail(State.TURKEY);
    }

    public void showStage3() { // sell wheat in turkey
        String string1 = mActivity.getString(R.string.REACH_STATE_AND_SELL_GOODS, getStateString(State.TURKEY), getGoodsString(Goods.WHEAT));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL, getGoodsString(Goods.WHEAT));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.ISRAEL, Goods.WHEAT, false);
        mFrontEnd.blinkMarket(Goods.WHEAT);
    }

    public void showStage4() { // buy olives in turkey.
        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS, getGoodsString(Goods.OLIVES), getStateString(State.TURKEY));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY, getGoodsString(Goods.OLIVES));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.blinkMarket(Goods.OLIVES);

        mFrontEnd.setStateVisibility(State.EGYPT, true);

        mFrontEnd.setGoodsVisibility(Goods.WHEAT, true);
        mFrontEnd.setGoodsVisibility(Goods.OLIVES, true);

        mLogic.mPriceTable.setPrice(State.TURKEY, Goods.OLIVES, 400);
        mLogic.mPriceTable.setPrice(State.EGYPT, Goods.OLIVES, 650);
        mLogic.mPriceTable.setPrice(State.ISRAEL, Goods.OLIVES, 350);
        mLogic.mPriceTable.setPrice(State.EGYPT, Goods.WHEAT, 35);

        mFrontEnd.highLightPrice(State.TURKEY, Goods.WHEAT, false);
        mFrontEnd.highLightPrice(State.TURKEY, Goods.OLIVES, true);

        mFrontEnd.showPrices();
    }

    public void showStage5() { // ship to egypt
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE_WITH_QEUSTION, getGoodsString(Goods.OLIVES));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE_WITH_QUESTION);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.EGYPT, Goods.OLIVES, true);
        mFrontEnd.blinkSail(State.EGYPT);
    }

    public void showStage6() { // sell olives in egypt
        String string1 = mActivity.getString(R.string.REACH_STATE_AND_SELL_GOODS_WITH_QUESTION, getStateString(State.EGYPT));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL_WITHOUT_STATE);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.TURKEY, Goods.OLIVES, false);
        mFrontEnd.blinkMarket(Goods.OLIVES);
    }

    public void showStage7() {
        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS_WITH_QUESTION, getStateString(State.EGYPT));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY_WITH_QUESTION);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.EGYPT, Goods.OLIVES, false);
        mFrontEnd.highLightPrice(State.EGYPT, Goods.WHEAT, true);
        mFrontEnd.blinkMarket(Goods.WHEAT);

        mFrontEnd.setStateVisibility(State.CYPRUS, true);
        mLogic.mPriceTable.setPrice(State.CYPRUS, Goods.OLIVES, 650);
        mLogic.mPriceTable.setPrice(State.CYPRUS, Goods.WHEAT, 70);
        mFrontEnd.showPrices();
    }

    public void showStage8() {
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE_WITH_QEUSTION, getGoodsString(Goods.WHEAT));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE_WITH_QUESTION);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.CYPRUS, Goods.WHEAT, true);
        mFrontEnd.blinkSail(State.CYPRUS);
    }

    public void showStage9() { // sell wheat in cyprus
        String string1 = mActivity.getString(R.string.REACH_STATE_AND_SELL_GOODS_WITH_QUESTION, getStateString(State.CYPRUS));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL_WITHOUT_STATE);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.EGYPT, Goods.WHEAT, false);
        mFrontEnd.blinkMarket(Goods.WHEAT);
    }

    public void showStage10() { // go to bank
        String string1 = getString(R.string.DEPOSIT_IN_BANK_BEFORE_SLEEP);
        String string2 = getString(R.string.PRESS_BANK_BUTTON_TO_DEPOSIT);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.setBankVisibility(true);
        mFrontEnd.highLightPrice(State.CYPRUS, Goods.WHEAT,false);
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
        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS_WITH_QUESTION, getStateString(State.CYPRUS));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY_WITH_QUESTION);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.CYPRUS, Goods.COPPER,true);
        mFrontEnd.blinkMarket(Goods.COPPER);
    }

    public void showStage14() { // sail to greece
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE_WITH_QEUSTION, getGoodsString(Goods.COPPER));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE_WITH_QUESTION);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.GREECE, Goods.COPPER,true);
        mFrontEnd.blinkSail(State.GREECE);
    }

    public void showStage15() { // fix ship
        String string1 = getString(R.string.WE_SHOUD_FIX_DAMAGED_SHIP);
        String string2 = getString(R.string.PRESS_ON_THE_FIX_BUTTON);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.setFixVisibility(true);
        mFrontEnd.blinkFixShip();
    }

    public void showStage16() { // sell copper in greece
        String string1 = mActivity.getString(R.string.REACH_STATE_AND_SELL_GOODS_WITH_QUESTION, getStateString(State.GREECE));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL_WITHOUT_STATE);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.CYPRUS, Goods.COPPER,false);
        mFrontEnd.blinkMarket(Goods.COPPER);
    }

    public void showStage17() { // buy wheat in greece
        String string1 = mActivity.getString(R.string.BUY_CHIP_GOODS_WITH_QUESTION, getStateString(State.GREECE));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_BUY_WITH_QUESTION);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.GREECE, Goods.COPPER,false);
        mFrontEnd.highLightPrice(State.GREECE, Goods.WHEAT,true);
        mFrontEnd.blinkMarket(Goods.WHEAT);
    }

    public void showStage18() { // sail to cyprus
        String string1 = mActivity.getString(R.string.SAIL_TO_STATE_WITH_HIGH_PRICE_WITH_QEUSTION, getGoodsString(Goods.WHEAT));
        String string2 = mActivity.getString(R.string.PRESS_ON_FLAG_TO_SAIL_TO_STATE_WITH_QUESTION);
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.CYPRUS, Goods.WHEAT,true);
        mFrontEnd.blinkSail(State.CYPRUS);
    }

    public void showStage19() { // sell wheat in cyprus
        String string1 = mActivity.getString(R.string.REACH_STATE_AND_SELL_MOST_GOODS, getStateString(State.CYPRUS), getGoodsString(Goods.WHEAT));
        String string2 = mActivity.getString(R.string.PRESS_GOODS_BUTTON_TO_SELL, getGoodsString(Goods.WHEAT));
        mFrontEnd.showTutorialStrings(string1, string2);
        mFrontEnd.highLightPrice(State.GREECE, Goods.WHEAT,false);
        mFrontEnd.blinkMarket(Goods.WHEAT);
    }

    public void onFlagClick(State destination) {
        if (mFrontEnd.isDelayedMessageExist()) {
            return;
        }

        switch (mStage) {
            case STAGE_1:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_BUY_FIRST, getGoodsString(Goods.WHEAT)), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.WHEAT);
                break;
            case STAGE_2:
                if (destination == State.TURKEY) {
                    mLogic.initSail(destination);
                    mFrontEnd.showWindow(Window.SAIL_WINDOW);
                    mFrontEndSail.initSailRoute();
                    mFrontEndSail.showOnlyStartSail();
                    mFrontEnd.showDelayedMessage(getString(R.string.PRESS_ON_V_TO_SAIL),
                            mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.TURKEY)));
                }
                break;
            case STAGE_3:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_SELL_FIRST, getGoodsString(Goods.WHEAT)), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.WHEAT);
                break;
            case STAGE_4:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_BUY_FIRST, getGoodsString(Goods.OLIVES)), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.OLIVES);
                break;
            case STAGE_5:
                if (destination == State.ISRAEL) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.GOODS_PRICE_IS_TO_LOW_IN_STATE,
                            getGoodsString(Goods.OLIVES), getStateString(State.ISRAEL)), getString(R.string.WE_SHOULD_NOT));
                    break;
                }
                mLogic.initSail(destination);
                mFrontEnd.showWindow(Window.SAIL_WINDOW);
                mFrontEndSail.initSailRoute();
                mFrontEndSail.showOnlyStartSail();
                mFrontEnd.showDelayedMessage(getString(R.string.PRESS_ON_V_TO_SAIL), mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.EGYPT)));
                break;
            case STAGE_6:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_SELL_SOMETHING_ELSE),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.OLIVES);
                break;
            case STAGE_7:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_BUY_FIRST_GOODS),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.WHEAT);
                break;
            case STAGE_17:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_BUY_FIRST_GOODS),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.WHEAT);
                break;
            case STAGE_8:
                if (destination == State.ISRAEL) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.GOODS_PRICE_HIGHER_IN_OTHER_STATE, getGoodsString(Goods.WHEAT)),
                            getString(R.string.WE_SHOULD_NOT));
                    break;
                }
                if (destination == State.TURKEY) {
                    mFrontEnd.showErrorMessage(getString(R.string.TO_LATE_FOR_LONG_SAIL), getString(R.string.WE_SHOULD_NOT));
                    break;
                }
                mLogic.initSail(destination);
                mFrontEnd.showWindow(Window.SAIL_WINDOW);
                mFrontEndSail.initSailRoute();
                mFrontEndSail.showAllButtons();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.GET_5_GUARDS), mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.CYPRUS)));
                break;
            case STAGE_9:
                mFrontEnd.showErrorMessage(getString(R.string.TO_LATE_FOR_SAIL), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.WHEAT);
                break;
            case STAGE_10:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.TO_LATE_FOR_SAIL_GO_TO_BANK), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkBank();
                break;
            case STAGE_11:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.TO_LATE_FOR_SAIL_GO_TO_SLEEP), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkSleep();
                break;
            case STAGE_12:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.DRAW_CASH_FIRST), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkBank();
                break;
            case STAGE_13:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_BUY_FIRST_GOODS),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.COPPER);
                break;
            case STAGE_14:
                if (destination == State.GREECE) {
                    mLogic.initSail(destination);
                    mFrontEnd.showWindow(Window.SAIL_WINDOW);
                    mFrontEndSail.initSailRoute();
                    mFrontEnd.showDelayedMessage(getString(R.string.GET_5_GUARDS), mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.GREECE)));
                } else {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.GOODS_PRICE_HIGHER_IN_OTHER_STATE,
                            getGoodsString(Goods.COPPER)), getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkSail(State.GREECE);
                }
                break;
            case STAGE_15:
                mFrontEnd.showErrorMessage(getString(R.string.FIX_SHIP_BEFORE_SAIL), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkFixShip();
                break;
            case STAGE_16:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_SELL_SOMETHING_ELSE), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.COPPER);
                break;
            case STAGE_18:
                mLogic.initSail(destination);
                mFrontEnd.showWindow(Window.SAIL_WINDOW);
                mFrontEndSail.initSailRoute();
                mFrontEnd.showDelayedMessage(getString(R.string.PRESS_ON_V_TO_SAIL), mActivity.getString(R.string.SHIP_TO_STATE_TITLE, getStateString(State.CYPRUS)));
                break;
            case STAGE_19:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_SELL_FIRST, getGoodsString(Goods.WHEAT)),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.WHEAT);
                break;
        }
    }

    public void onMarketClick(Goods goods) {
        if (mFrontEnd.isDelayedMessageExist()) {
            return;
        }

        switch (mStage) {
            case STAGE_1:
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.BUY_ALL)),
                        mActivity.getString(R.string.BUY_GOODS_TITLE, getGoodsString(Goods.WHEAT)));
                break;
            case STAGE_2:
                mFrontEnd.showErrorMessage(getString(R.string.BUY_ENOUGH_NOW_SAIL), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkSail(State.TURKEY);
                break;
            case STAGE_3:
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.SELL_ALL)),
                        mActivity.getString(R.string.SELL_GOODS_TITLE, getGoodsString(Goods.WHEAT)));
                break;
            case STAGE_4:
                if (goods == Goods.WHEAT) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.DONT_BUY_EXPENSIVE_GOODS, getGoodsString(Goods.WHEAT)),
                            getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkMarket(Goods.OLIVES);
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.BUY_ALL)),
                        mActivity.getString(R.string.BUY_GOODS_TITLE, getGoodsString(Goods.OLIVES)));
                break;
            case STAGE_5:
                mFrontEnd.showErrorMessage(getString(R.string.BUY_ENOUGH_NOW_SAIL), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkSail(State.EGYPT);
                break;
            case STAGE_8:
            case STAGE_18:
                mFrontEnd.showErrorMessage(getString(R.string.BUY_ENOUGH_NOW_SAIL), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkSail(State.CYPRUS);
                break;
            case STAGE_14:
                mFrontEnd.showErrorMessage(getString(R.string.BUY_ENOUGH_NOW_SAIL), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkSail(State.GREECE);
                break;
            case STAGE_6:
                if (goods == Goods.WHEAT) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_SELL_SOMETHING_ELSE),
                            getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkMarket(Goods.OLIVES);
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.SELL_ALL)),
                        mActivity.getString(R.string.SELL_GOODS_TITLE, getGoodsString(Goods.OLIVES)));
                break;
            case STAGE_7:
                if (goods == Goods.OLIVES) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.CHEAPPER_GOOD_TO_BUY),
                            getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkMarket(Goods.WHEAT);
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyFillCapacity();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.FILL_CAPACITY)),
                        mActivity.getString(R.string.BUY_GOODS_TITLE, getGoodsString(Goods.WHEAT)));
                break;
            case STAGE_9:
                if (goods == Goods.OLIVES) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.LETS_SELL_OTHER_GOODS),
                            getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkMarket(Goods.WHEAT);
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.SELL_ALL)),
                        mActivity.getString(R.string.SELL_GOODS_TITLE, getGoodsString(Goods.WHEAT)));
                break;
            case STAGE_10:
                mFrontEnd.showErrorMessage(getString(R.string.TO_LATE_FOR_MARKET_GO_TO_BANK), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkBank();
                break;
            case STAGE_11:
                mFrontEnd.showErrorMessage(getString(R.string.TO_LATE_FOR_MARKET_GO_TO_SLEEP), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkSleep();
                break;
            case STAGE_12:
                mFrontEnd.showErrorMessage(getString(R.string.DRAW_FIRST_CACH_FROM_BANK), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkBank();
                break;
            case STAGE_13:
                if (goods != Goods.COPPER) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.DONT_BUY_EXPENSIVE_GOODS, getGoodsString(goods)),
                            getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkMarket(Goods.COPPER);
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.BUY_ALL)),
                        mActivity.getString(R.string.BUY_GOODS_TITLE, getGoodsString(Goods.COPPER)));
                break;
            case STAGE_15:
                mFrontEnd.showErrorMessage(getString(R.string.FIX_SHIP_FIRST), getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkFixShip();
                break;
            case STAGE_16:
                if (goods != Goods.COPPER) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.LETS_SELL_OTHER_GOODS),
                            getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkMarket(Goods.COPPER);
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.SELL_ALL)),
                        mActivity.getString(R.string.SELL_GOODS_TITLE, getGoodsString(Goods.COPPER)));
                break;
            case STAGE_17:
                if (goods != Goods.WHEAT) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.CHEAPPER_GOOD_TO_BUY),
                            getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkMarket(Goods.WHEAT);
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlyBuyAllButton();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.BUY_ALL)),
                        mActivity.getString(R.string.BUY_GOODS_TITLE, getGoodsString(Goods.WHEAT)));
                break;
            case STAGE_19:
                if (goods != Goods.WHEAT) {
                    mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_SELL_FIRST, getGoodsString(Goods.WHEAT)),
                            getString(R.string.WE_SHOULD_NOT));
                    mFrontEnd.blinkMarket(Goods.WHEAT);
                    break;
                }
                mLogic.initMarketDeal(goods);
                mFrontEndMarket.onMarketClick();
                mFrontEndMarket.showOnlySellAllButton();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.SELL_ALL)),
                        mActivity.getString(R.string.SELL_GOODS_TITLE, getGoodsString(Goods.WHEAT)));
                break;
        }
    }

    public void onMarketDealDoneClick() {
        mFrontEnd.cancelDelayedMessage();

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
        mFrontEnd.cancelDelayedMessage();

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
        if (mFrontEnd.isDelayedMessageExist()) {
            return;
        }

        switch (mStage) {
            case STAGE_10:
                mLogic.initBankDeal();
                mFrontEnd.showWindow(Window.BANK_WINDOW);
                mFrontEndBank.onBankClick();
                mFrontEndBank.showOnlyDepositAll();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.BANK_DEPOSIT_ALL)),
                        getString(R.string.BANK_DEPOSIT_TITLE));

                break;
            case STAGE_12:
                mLogic.initBankDeal();
                mFrontEnd.showWindow(Window.BANK_WINDOW);
                mFrontEndBank.onBankClick();
                mFrontEndBank.showOnlyDrawAll();
                mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.BANK_DRAW_ALL)),
                        getString(R.string.BANK_DRAW_TITLE));
                break;
            case STAGE_13:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.LETS_BUY_SOME_GOODS),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.COPPER);
                break;
            case STAGE_14:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.SAIL_TO_SOMEWHERE),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkSail(State.GREECE);
                break;
            case STAGE_15:
                mFrontEnd.showErrorMessage(getString(R.string.FIX_SHIP_FIRST),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkFixShip();
                break;
            case STAGE_16:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_SELL_SOMETHING_ELSE),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.COPPER);
                break;
            case STAGE_17:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_BUY_GOODS),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.WHEAT);
                break;
            case STAGE_18:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.SAIL_TO_SOMEWHERE),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkSail(State.CYPRUS);
                break;
            case STAGE_19:
                mFrontEnd.showErrorMessage(mActivity.getString(R.string.WE_SHOULD_SELL_FIRST, getGoodsString(Goods.WHEAT)),
                        getString(R.string.WE_SHOULD_NOT));
                mFrontEnd.blinkMarket(Goods.WHEAT);
                break;
        }
    }

    public boolean checkIfCanSail() {
         switch (mStage) {
            case STAGE_8:
            case STAGE_14:
                if (mLogic.mSail.mSelectedNumGuardShips < 5) {
                    mFrontEnd.showErrorMessage(getString(R.string.TAKE_5_GUARDS_SHIPS), getString(R.string.WE_SHOULD_NOT));
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
                mLogic.mSail.mSailEndedPeacefully = false;
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
        if (mFrontEnd.isDelayedMessageExist()) {
            return;
        }

        mLogic.initFixShipDeal();
        mFrontEndFixShip.onFixShipClick();
        mFrontEnd.showWindow(Window.FIX_SHIP_WINDOW);
        mFrontEnd.showDelayedMessage(mActivity.getString(R.string.PRESS_ON_BUTTON_AND_ON_V, getString(R.string.FIXED_AS_POSSIBLE)),
                 getString(R.string.FIX_SHIP_TITLE));
    }

    public void fixDone() {
        mFrontEnd.cancelDelayedMessage();

        if (mLogic.mDamage == 0) {
            mStage = TutorialStage.STAGE_16;
            showStage16();
        }
    }
}
