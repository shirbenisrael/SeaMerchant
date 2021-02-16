package com.shirbi.seamerchant;

import android.widget.TextView;

import androidx.annotation.IdRes;

import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class FrontEndTimeAnimation extends FrontEndGeneric {
    private Timer mTimer;
    private TimerTask mTimerTask;

    TextView mTextView;
    int mCurrentFrameNumber;
    int mStartHour;
    int mEndHour;

    static final int MAX_FRAMES = 25;
    static final int FRAME_DURATION_MILLISECONDS = 40;

    public FrontEndTimeAnimation(MainActivity activity, @IdRes int viewId) {
        super(activity);
        mTextView = mActivity.findViewById(viewId);
    }

    private FrontEndTimeAnimation getThis() { return this; }

    public void updateTime(int endHour) {
        int startHour;
        try {
            StringTokenizer st = new StringTokenizer(mTextView.getText().toString(), ":");
            startHour = Integer.parseInt(st.nextToken());
        } catch(Exception ignored) {
            startHour = endHour;
        }

        updateTime(startHour, endHour);
    }

    public void updateTime(int startHour,int endHour) {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mStartHour = startHour;
        mEndHour = endHour;

        if (mStartHour == mEndHour) {
            showText(mEndHour, 0);
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
            float totalDuration = (mEndHour - mStartHour);
            float timePassed = totalDuration * mCurrentFrameNumber / MAX_FRAMES + mStartHour;

            int timePassedHours =(int)timePassed;
            int timePassedMinutes = (int)((timePassed - timePassedHours) * 60);
            showText(timePassedHours, timePassedMinutes);

            if (mCurrentFrameNumber >= MAX_FRAMES) {
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }

                if (mTimerTask != null) {
                    mTimerTask.cancel();
                    mTimerTask = null;
                }
                showText(mEndHour, 0);
            }
        }
    };

    public void showText(int hours, int minutes) {
        mTextView.setText(mActivity.getString(R.string.HOUR_MINUTES_STRING, hours, minutes));
    }
}
