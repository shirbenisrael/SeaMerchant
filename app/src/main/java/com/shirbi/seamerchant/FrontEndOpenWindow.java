package com.shirbi.seamerchant;

import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class FrontEndOpenWindow extends FrontEndGeneric {
    public FrontEndOpenWindow(MainActivity activity) {
        super(activity);
    }

    private int mTextIds[] = {
            R.string.OPEN_SCREEN_1,
            0,
            R.string.OPEN_SCREEN_2,
            0,
            R.string.OPEN_SCREEN_3,
            R.string.OPEN_SCREEN_4,
            0,
            R.string.OPEN_SCREEN_5,
            R.string.OPEN_SCREEN_6,
            R.string.OPEN_SCREEN_7,
            0,
            R.string.OPEN_SCREEN_8,
            R.string.OPEN_SCREEN_9,
            R.string.OPEN_SCREEN_10,
            R.string.OPEN_SCREEN_11,
            0,
            R.string.OPEN_SCREEN_12,
            0,
            R.string.OPEN_SCREEN_13
    };

    private Timer mTimer;
    private TimerTask mTimerTask;
    private int mMessageNum = 0;
    private int mIterator = 0;
    private TextView mTextView = findViewById(R.id.open_message);

    public void startAnimate() {
        mMessageNum = 0;
        mIterator = 0;
        startTimer();
        updateMessage();
    }

    private void updateMessage() {
        if (mTextIds[mMessageNum] == 0) {
            mTextView.setText("");
        } else {
            mTextView.setText(getString(mTextIds[mMessageNum]));
        }
    }

    private void startTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimerTask = (new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(mTimerTick);
            }
        });
        mTimer.schedule(mTimerTask,0, 25);
    }

    private void placeTextAccordingIterator() {
        mTextView.setPadding(0,mIterator * 4,0,0);
        if (mIterator < 50) {
            mTextView.setAlpha(1.0f);
        } else {
            float opacity = (100.0f - mIterator) / 50.0f;
            mTextView.setAlpha(opacity);
        }
    }

    private void resetIteratorIfNeeded() {
        if (mMessageNum >= mTextIds.length) {
            return;
        }

        if (mIterator == 100 || (mIterator == 50 && (mTextIds[mMessageNum] == 0))) {
            mIterator = 0;
            mMessageNum++;
            if (mMessageNum >= mTextIds.length) {
                mTimerTask.cancel();
                mTimer.cancel();
            } else {
                updateMessage();
                placeTextAccordingIterator();
                findViewById(R.id.start_play_button).setVisibility(View.VISIBLE);
            }
        }
    }

    Runnable mTimerTick = new Runnable() {
        public void run() {
            placeTextAccordingIterator();
            mIterator++;
            resetIteratorIfNeeded();
        }
    };
}
