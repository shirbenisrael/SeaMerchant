package com.shirbi.seamerchant;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class FrontEndInflater extends FrontEndGeneric {
    public FrontEndInflater(MainActivity activity) {
        super(activity);
    }

    static final int MAX_FRAMES = 25;
    static final int FRAME_DURATION_MILLISECONDS = 100;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private int mCurrentFrameNumber;
    ImageView mImageView;
    boolean mIsFadeOut;

    public void inflate(ImageView imageView) {
        stopTimer();
        mImageView = imageView;

        mCurrentFrameNumber = 0;

        mTimer = new Timer();
        mTimerTask = (new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(mTimerTick);
            }
        });
        mIsFadeOut = false;
        mTimer.schedule(mTimerTask, mLogic.generateRandom(8000), FRAME_DURATION_MILLISECONDS);
        mImageView.setAlpha(0.0f);
    }

    Runnable mTimerTick = new Runnable() {
        public void run() {
            mCurrentFrameNumber++;

            if (mIsFadeOut) {
                mImageView.setAlpha(1.0f - (float) mCurrentFrameNumber / MAX_FRAMES);
            } else {
                mImageView.setAlpha((float) mCurrentFrameNumber / MAX_FRAMES);
            }

            mImageView.getLayoutParams().width += 4;
            mImageView.getLayoutParams().height += 4;
            ((RelativeLayout.LayoutParams)mImageView.getLayoutParams()).leftMargin -= 2;
            ((RelativeLayout.LayoutParams)mImageView.getLayoutParams()).topMargin -= 2;

            if (mCurrentFrameNumber >= MAX_FRAMES) {
                if (mIsFadeOut) {
                    stopTimer();
                } else {
                    mIsFadeOut = true;
                    mCurrentFrameNumber = 0;
                }
            }
        }
    };

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}
