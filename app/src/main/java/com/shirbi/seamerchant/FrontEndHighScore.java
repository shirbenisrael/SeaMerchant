package com.shirbi.seamerchant;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FrontEndHighScore extends FrontEndGeneric {
    public FrontEndHighScore(MainActivity activity) {
        super(activity);
        createEmptyScoreTable();
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

    private void createEmptyScoreTable() {
        LinearLayout table = findViewById(R.id.scores_layout);
        for (int i = 0; i < mLogic.mScoreTable.length; i++) {
            LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.one_high_score_layout, table);
        }

        fillScores();
    }

    private void fillScores() {
        LinearLayout table = findViewById(R.id.scores_layout);
        for (int i = 0; i < mLogic.mScoreTable.length; i++) {
            TextView rank = (TextView) ((LinearLayout) table.getChildAt(i)).getChildAt(0);
            rank.setText(String.valueOf( mLogic.mScoreTable[i].rank));

            TextView name = (TextView) ((LinearLayout) table.getChildAt(i)).getChildAt(1);
            name.setText(mLogic.mScoreTable[i].name);

            TextView score = (TextView) ((LinearLayout) table.getChildAt(i)).getChildAt(2);
            score.setText(String.valueOf(mLogic.mScoreTable[i].score));
        }
    }
}
