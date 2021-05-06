package com.shirbi.seamerchant;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class FrontEndInflater extends FrontEndGeneric {
    public FrontEndInflater(MainActivity activity) {
        super(activity);
        mMaxFrames = DEFAULT_MAX_FRAMES;
        mFrameDurationMilliseconds = DEFAULT_FRAME_DURATION_MILLISECONDS;
        mMaxDelayMilliseconds = DEFAULT_MAX_DELAY_MILLISECONDS;
    }

    public FrontEndInflater(MainActivity activity, int maxFrames, int frameDuration, int maxDelayMilliseconds) {
        super(activity);
        mMaxFrames = maxFrames;
        mFrameDurationMilliseconds = frameDuration;
        mMaxDelayMilliseconds = maxDelayMilliseconds;
    }

    private int mMaxFrames;
    private int mFrameDurationMilliseconds;
    private int mMaxDelayMilliseconds;

    static final int DEFAULT_MAX_FRAMES = 25;
    static final int DEFAULT_FRAME_DURATION_MILLISECONDS = 100;
    static final int DEFAULT_MAX_DELAY_MILLISECONDS = 35000;

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
        mTimer.schedule(mTimerTask, mLogic.generateRandom(mMaxDelayMilliseconds), mFrameDurationMilliseconds);
        mImageView.setAlpha(0.0f);
    }

    Runnable mTimerTick = new Runnable() {
        public void run() {
            mCurrentFrameNumber++;

            if (mIsFadeOut) {
                mImageView.setAlpha(1.0f - (float) mCurrentFrameNumber / mMaxFrames);
            } else {
                mImageView.setAlpha((float) mCurrentFrameNumber / mMaxFrames);
            }

            mImageView.getLayoutParams().width += 4;
            mImageView.getLayoutParams().height += 4;
            ((RelativeLayout.LayoutParams)mImageView.getLayoutParams()).leftMargin -= 2;
            ((RelativeLayout.LayoutParams)mImageView.getLayoutParams()).topMargin -= 2;

            if (mCurrentFrameNumber >= mMaxFrames) {
                if (mIsFadeOut) {
                    stopTimer();
                    if (mImageView.getParent() != null) {
                        ((ViewGroup)mImageView.getParent()).removeView(mImageView);
                    }
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
