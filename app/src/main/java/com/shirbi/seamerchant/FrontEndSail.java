package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;

import java.util.Timer;
import java.util.TimerTask;

public class FrontEndSail extends FrontEndGeneric {
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Point mMapSize;
    private float mProgress;
    private @IdRes int mImageToAnimate;
    private boolean mRealSail;

    public FrontEndSail(MainActivity activity) {
        super(activity);
    }

    private Point getWindowSize() {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private void calculateMapSize() {
        mMapSize = getWindowSize(); // need to adjust y

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.map_container);
        float weight = ((LinearLayout.LayoutParams)relativeLayout.getLayoutParams()).weight;

        LinearLayout sailLayout = findViewById(R.id.sail_layout);
        float totalWeights = 0;
        for (int i = 0 ; i < sailLayout.getChildCount(); i++) {
            View view = sailLayout.getChildAt(i);
            totalWeights += ((LinearLayout.LayoutParams)view.getLayoutParams()).weight;
        }

        mMapSize.y *= weight / totalWeights;
    }

    void putObjectOnMap(@IdRes int id, float x, float y, float width, float height) {
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.map_container);
        View object = findViewById(id);

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams((int)(width * mMapSize.x), (int)(height * mMapSize.x));

        params.leftMargin = (int)(x * mMapSize.x);
        params.topMargin = (int)(y * mMapSize.y);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        relativeLayout.removeView(object);
        relativeLayout.addView(object, params);
    }

    private void putBoatOnHarbor() {
        putObjectOnMap(R.id.boat_on_map,
                mLogic.mSail.mSource.toLocationX(),
                mLogic.mSail.mSource.toLocationY(),
                0.10f, 0.10f);

        ImageView boat = findViewById(R.id.boat_on_map);

        if (mLogic.mSail.mSource.toLocationX() > mLogic.mSail.mDestination.toLocationX()) {
            boat.setImageResource(R.drawable.boat_left);
        } else {
            boat.setImageResource(R.drawable.boat_right);
        }
    }

    public void initSailRoute() {
        calculateMapSize();
        putBoatOnHarbor();

        putObjectOnMap(R.id.destination,
                mLogic.mSail.mDestination.toLocationX(),
                mLogic.mSail.mDestination.toLocationY(),
                0.05f, 0.10f);

        mProgress = 0.0f;
        mImageToAnimate = R.id.circle_on_map;
        mRealSail = false;
        findViewById(R.id.circle_on_map).setVisibility(View.VISIBLE);

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

        mTimer.schedule(mTimerTask,0, 100);
    }

    Runnable mTimerTick = new Runnable() {
        public void run() {
            float startX, startY;
            float endX, endY;
            float locationX, locationY;
            float actualProgress;

            if (mProgress < 0.5f) {
                startX = mLogic.mSail.mSource.toLocationX();
                startY = mLogic.mSail.mSource.toLocationY();
                endX = mLogic.mSail.mSource.toMiddlePointX(mLogic.mSail.mDestination);
                endY = mLogic.mSail.mSource.toMiddlePointY(mLogic.mSail.mDestination);
                actualProgress = mProgress * 2;
            } else {
                startX = mLogic.mSail.mSource.toMiddlePointX(mLogic.mSail.mDestination);
                startY = mLogic.mSail.mSource.toMiddlePointY(mLogic.mSail.mDestination);
                endX = mLogic.mSail.mDestination.toLocationX();
                endY = mLogic.mSail.mDestination.toLocationY();
                actualProgress = (mProgress - 0.5f) * 2;
            }

            locationX = actualProgress * endX + (1.0f - actualProgress) * startX;
            locationY = actualProgress * endY + (1.0f - actualProgress) * startY;

            putObjectOnMap(mImageToAnimate,
                    locationX,
                    locationY,
                    0.05f, 0.05f);

            mProgress += 0.02f;

            if (mProgress > 1.0f) {
                if (mRealSail) {
                    mTimer.cancel();
                    sailEnd();
                } else {
                    mProgress = 0.0f;
                }
            }
        }
    };

    public void startSail() {
        findViewById(R.id.circle_on_map).setVisibility(View.GONE);
        mProgress = 0;
        mImageToAnimate = R.id.boat_on_map;
        mRealSail = true;
    }

    private void sailEnd() {
        mLogic.finishSail();
        mActivity.mFrontEnd.exitSail();
        mActivity.mFrontEnd.showState();
    }
}
