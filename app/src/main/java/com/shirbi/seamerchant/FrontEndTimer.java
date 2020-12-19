package com.shirbi.seamerchant;

import java.util.Timer;
import java.util.TimerTask;

public class FrontEndTimer {
    private Timer mTimer;
    private TimerTask mTimerTask;
    MainActivity mActivity;
    FrontEndGeneric mFrontEndGeneric;
    int mTimes;

    public FrontEndTimer(FrontEndGeneric frontEndGeneric) {
        mFrontEndGeneric = frontEndGeneric;
        mActivity = frontEndGeneric.mActivity;
    }

    private FrontEndTimer getThis() { return this; }

    public void startTimer(long milliseconds, int times) {
        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimes = times;
        mTimer = new Timer();
        mTimerTask = (new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(mTimerTick);
            }
        });
        mTimer.schedule(mTimerTask,0, milliseconds);
    }

    Runnable mTimerTick = new Runnable() {
        public void run() {
            mTimes--;
            mFrontEndGeneric.timerBlinked(getThis() , mTimes);

            if (mTimes <= 0) {
                mTimer.cancel();
                mTimer = null;
                mTimerTask = null;
            }
        }
    };
}
