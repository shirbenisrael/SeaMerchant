package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndHighScore extends FrontEndGeneric {
    public FrontEndHighScore(MainActivity activity) {
        super(activity);
    }

    public void showHighScoreAtGameEnd() {
        String message = mActivity.getString(R.string.GAME_RESULT, mLogic.calculateTotalValue());
        ((TextView)findViewById(R.id.current_score)).setText(message);

        message = mActivity.getString(R.string.YOUR_HIGHEST_SCORE, mLogic.mHighScore);
        ((TextView)findViewById(R.id.high_score)).setText(message);
    }

    public void showHighScoreWhilePlaying() {
        String message = mActivity.getString(R.string.YOUR_CURRENT_SCORE, mLogic.calculateTotalValue());
        ((TextView)findViewById(R.id.current_score)).setText(message);

        message = mActivity.getString(R.string.YOUR_HIGHEST_SCORE, mLogic.mHighScore);
        ((TextView)findViewById(R.id.high_score)).setText(message);
    }
}
