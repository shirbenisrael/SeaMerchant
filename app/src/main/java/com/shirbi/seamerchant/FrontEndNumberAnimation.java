package com.shirbi.seamerchant;

import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import java.util.Timer;
import java.util.TimerTask;

public class FrontEndNumberAnimation extends FrontEndGeneric {
    private Timer mTimer;
    private TimerTask mTimerTask;

    TextView mTextView;
    @StringRes int mPrefix;
    int mCurrentFrameNumber;
    long mStartNumber;
    long mEndNumber;

    static final int MAX_FRAMES = 25;
    static final int FRAME_DURATION_MILLISECONDS = 40;

    public FrontEndNumberAnimation(MainActivity activity, @IdRes int viewId, @StringRes int prefix) {
        super(activity);
        mTextView = mActivity.findViewById(viewId);
        mPrefix = prefix;
    }

    public FrontEndNumberAnimation(MainActivity activity, TextView view, @StringRes int prefix) {
        super(activity);
        mTextView = view;
        mPrefix = prefix;
    }

    private FrontEndNumberAnimation getThis() { return this; }

    public void changeNumber(long endNumber) {
        if (mTimer != null) {
            mTimer.cancel();
        }

        mEndNumber = endNumber;
        try {
            mStartNumber = Long.parseLong(mTextView.getText().toString().replaceAll("\\D+", ""));
        } catch(Exception ignored) {
            mStartNumber = 0;
        }
        if (mStartNumber == mEndNumber) {
            showText(mEndNumber);
            return;
        }

        mCurrentFrameNumber = 0;
        mTimer = new Timer();
        mTimerTask = (new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(mTimerTick);
            }
        });
        mTimer.schedule(mTimerTask,0, FRAME_DURATION_MILLISECONDS);
    }

    Runnable mTimerTick = new Runnable() {
        public void run() {
            mCurrentFrameNumber++;
            long number = (mEndNumber - mStartNumber) * mCurrentFrameNumber / MAX_FRAMES + mStartNumber;
            showText(number);

            if (mCurrentFrameNumber >= MAX_FRAMES) {
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }

                if (mTimerTask != null) {
                    mTimerTask.cancel();
                    mTimerTask = null;
                }
                showText(mEndNumber);
            }
        }
    };

    private void showText(long number) {
        if (mPrefix != 0) {
            mTextView.setText(mActivity.getString(mPrefix, mDecimalFormat.format(number)));
        } else {
            mTextView.setText(mDecimalFormat.format(number));
        }
    }
}
