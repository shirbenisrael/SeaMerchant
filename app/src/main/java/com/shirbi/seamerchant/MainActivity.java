package com.shirbi.seamerchant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    Logic mLogic;
    FrontEnd mFrontEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogic = new Logic();
        mFrontEnd = new FrontEnd(this);

        mLogic.startNewGame();
        mFrontEnd.ShowState();
    }

    public void onMarketClick(View view) {
        Goods goods = mFrontEnd.viewToGoods(view);
        mLogic.initMarketDeal(goods);

        mFrontEnd.onMarketClick();
    }

    public void onMarketBuyAllClick(View view) {
        mLogic.mMarketDeal.buyAll();
        mFrontEnd.showDealState();
    }

    public void onDealDoneClick(View view) {
        mFrontEnd.onDealDoneClick(null);
    }

}