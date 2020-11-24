package com.shirbi.seamerchant;

import android.app.Activity;
import android.os.Bundle;

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
}