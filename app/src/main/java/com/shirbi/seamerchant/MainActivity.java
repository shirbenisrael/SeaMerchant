package com.shirbi.seamerchant;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    FrontEnd mFrontEnd = new FrontEnd(this);
    Logic mLogic = new Logic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogic.startNewGame();
        mFrontEnd.ShowPrices(mLogic.priceTable);
    }
}