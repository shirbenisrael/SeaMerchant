package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndHighScore extends FrontEndGeneric {
    public FrontEndHighScore(MainActivity activity) {
        super(activity);
    }

    public void showHighScore() {
        String message = mActivity.getString(R.string.GAME_RESULT, mLogic.calculateTotalValue());
        ((TextView)findViewById(R.id.high_score_title)).setText(message);
    }
}
