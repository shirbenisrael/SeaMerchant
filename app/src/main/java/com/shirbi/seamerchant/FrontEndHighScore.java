package com.shirbi.seamerchant;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

public class FrontEndHighScore extends FrontEndGeneric {
    private boolean currentlyShowEndResult = false;
    private DecimalFormat mDecimalFormat;

    public FrontEndHighScore(MainActivity activity) {
        super(activity);
        mDecimalFormat = new DecimalFormat("#,###");
        createEmptyScoreTable();
    }

    public void showHighScoreAtGameEnd() {
        currentlyShowEndResult = true;
        showScores(Logic.ScoreType.HIGH_SCORE_TABLE_INDEX);
    }

    public void showHighScoreWhilePlaying() {
        currentlyShowEndResult = false;
        showScores(Logic.ScoreType.HIGH_SCORE_TABLE_INDEX);
    }

    private void updateTopMessage() {
        String message1;
        String message2;

        if (currentlyShowEndResult) {
            if (findViewById(R.id.capacity_layout).getVisibility() == View.VISIBLE) {
                message1 = mActivity.getString(R.string.GAME_RESULT_CAPACITY, mLogic.mCapacity);
                message2 = mActivity.getString(R.string.YOUR_HIGHEST_CAPACITY, mLogic.mHighCapacity);
            } else {
                message1 = mActivity.getString(R.string.GAME_RESULT_HIGH_SCORE, mDecimalFormat.format((mLogic.calculateTotalValue())));
                message2 = mActivity.getString(R.string.YOUR_HIGHEST_SCORE, mDecimalFormat.format(mLogic.mHighScore));
            }
        } else {
            if (findViewById(R.id.capacity_layout).getVisibility() == View.VISIBLE) {
                message1 = mActivity.getString(R.string.YOUR_CURRENT_CAPACITY, mLogic.mCapacity);
                message2 = mActivity.getString(R.string.YOUR_HIGHEST_CAPACITY, mLogic.mHighCapacity);
            } else {
                message1 = mActivity.getString(R.string.YOUR_CURRENT_SCORE, mDecimalFormat.format(mLogic.calculateTotalValue()));
                message2 = mActivity.getString(R.string.YOUR_HIGHEST_SCORE, mDecimalFormat.format(mLogic.mHighScore));
            }
        }

        ((TextView)findViewById(R.id.current_score)).setText(message1);
        ((TextView)findViewById(R.id.high_score)).setText(message2);
    }

    private void createEmptyScoreTable() {
        int[] layoutIds = {R.id.scores_layout, R.id.capacity_layout};
        for (int tableId = 0; tableId < layoutIds.length; tableId++) {
            LinearLayout table = findViewById(layoutIds[tableId]);
            for (int i = 0; i < mLogic.mScoreTable[tableId].length; i++) {
                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.one_high_score_layout, table);
            }
        }

        fillScores();
    }

    public void fillScores() {
        int[] layoutIds = {R.id.scores_layout, R.id.capacity_layout};
        for (int tableId = 0; tableId < layoutIds.length; tableId++) {
            LinearLayout table = findViewById(layoutIds[tableId]);
            for (int i = 0; i < mLogic.mScoreTable[tableId].length; i++) {
                Logic.ScoreTable oneScore = mLogic.mScoreTable[tableId][i];
                int color = (mLogic.mRank[tableId] == oneScore.rank) ? mActivity.getColor(R.color.transparent_red) :
                        mActivity.getColor(R.color.transparent);

                TextView rank = (TextView) ((LinearLayout) table.getChildAt(i)).getChildAt(0);
                rank.setText(String.valueOf(oneScore.rank));
                rank.setBackgroundColor(color);

                TextView name = (TextView) ((LinearLayout) table.getChildAt(i)).getChildAt(1);
                name.setText(oneScore.name);
                name.setBackgroundColor(color);

                TextView score = (TextView) ((LinearLayout) table.getChildAt(i)).getChildAt(2);
                score.setText(mDecimalFormat.format(oneScore.score));
                score.setBackgroundColor(color);
            }
        }
    }

    public void showScores(Logic.ScoreType scoreType) {
        int[] layoutIds = {R.id.scores_layout, R.id.capacity_layout};

        findViewById(layoutIds[scoreType.getValue()]).setVisibility(View.VISIBLE);
        findViewById(layoutIds[1 -scoreType.getValue()]).setVisibility(View.GONE);

        updateTopMessage();
    }
}
