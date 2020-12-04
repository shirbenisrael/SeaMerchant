package com.shirbi.seamerchant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    Logic mLogic;
    FrontEnd mFrontEnd;
    FrontEndMarket mFrontEndMarket;
    FrontEndSail mFrontEndSail;
    FrontEndPirates mFrontEndPirates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogic = new Logic();
        mFrontEnd = new FrontEnd(this);
        mFrontEndMarket = new FrontEndMarket(this);
        mFrontEndSail = new FrontEndSail(this);
        mFrontEndPirates = new FrontEndPirates(this);

        mLogic.startNewGame();
        mFrontEnd.showState();
    }

    public void onMarketClick(View view) {
        Goods goods = mFrontEnd.viewToGoods(view);
        mLogic.initMarketDeal(goods);

        mFrontEndMarket.onMarketClick();
    }

    public void onMarketMinusTenClick(View view) {
        mLogic.mMarketDeal.removeGoods(10);
        mFrontEndMarket.showDealState();
    }

    public void onMarketMinusOneClick(View view) {
        mLogic.mMarketDeal.removeGoods(1);
        mFrontEndMarket.showDealState();
    }

    public void onMarketPlusOneClick(View view) {
        mLogic.mMarketDeal.addGoods(1);
        mFrontEndMarket.showDealState();
    }

    public void onMarketPlusTenClick(View view) {
        mLogic.mMarketDeal.addGoods(10);
        mFrontEndMarket.showDealState();
    }

    public void onMarketBuyAllClick(View view) {
        mLogic.mMarketDeal.buyAll();
        mFrontEndMarket.showDealState();
    }

    public void onMarketSellAllClick(View view) {
        mLogic.mMarketDeal.sellAll();
        mFrontEndMarket.showDealState();
    }

    public void onMarketFillCapacityClick(View view) {
        mLogic.mMarketDeal.fillCapacity();
        mFrontEndMarket.showDealState();
    }

    public void onMarketPercentageForGuardsClick(View view) {
        mLogic.mMarketDeal.leaveCashForGuards();
        mFrontEndMarket.showDealState();
    }

    public void onDealDoneClick(View view) {
        mLogic.applyMarketDeal();
        mFrontEnd.showState();
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onDealCancelClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onFlagClick(View view) {
        State destination = mFrontEnd.viewToState(view);
        if (mLogic.getSailDuration(destination) == 0) {
            return;
        }

        mLogic.initSail(destination);
        mFrontEndSail.initSailRoute();
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
    }

    public void onSailCancelClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onSailClick(View view) {
        // TODO: start sail in logic - change time, reduce cash of guards...

        mFrontEndSail.startSail();
    }

    public void onSailEndClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();
    }

    public void onSleepClick(View view) {
        mFrontEnd.showWindow(Window.SLEEP_WINDOW);
    }

    public void onCancelSleep(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onApproveSleep(View view) {
        mLogic.startNewDay();
        mFrontEnd.showWindow(Window.WEATHER_WINDOW);
        mFrontEnd.showNewWeather();
    }

    public void onExitWeatherWindow(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();
    }

    public void onGuardShipClick(View view) {
        mFrontEndSail.guardShipClick(view);
    }

    public void onAttackClick(View view) {
        mLogic.mSail.calculateBattleResult();
        if (mLogic.mSail.mBattleResult == Sail.BattleResult.LOSE) {
            mFrontEndPirates.showLoseToPiratesMessage();
        } else {
            mFrontEndPirates.showWinPiratesMessage();
        }
        mFrontEnd.showWindow(Window.PIRATES_ATTACK_WINDOW);
    }

    public void onEscapeClick(View view) {
        if (mLogic.mSail.isEscapePiratesSucceeds()) {
            mFrontEnd.showWindow(Window.ESCAPE_WINDOW);
        } else {
            mFrontEndPirates.showFailEscapeMessage();
        }
    }

    public void onNegotiateClick(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndSail.continueSail();
    }

    public void onEscapeDoneClick(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndSail.continueSail();
    }

    public void onBattleWinDoneClick(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndSail.continueSail();
    }

    public void showPirates() {
        mFrontEnd.showWindow(Window.PIRATES_WINDOW);
        mFrontEndPirates.showChances();
    }


    public void onBankClick(View view) {
        mFrontEnd.showWindow(Window.BANK_WINDOW);
    }

    public void onBankMinus10000Click(View view) {
    }

    public void onBankMinus1000Click(View view) {
    }

    public void onBankPlus1000Click(View view) {
    }

    public void onBankPlus10000Click(View view) {
    }

    public void onDrawAllClick(View view) {
    }

    public void onCashForGuardsClick(View view) {
    }

    public void onDepositAllClick(View view) {
    }

    public void onBankDealCancelClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onBankDealDoneClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }
}