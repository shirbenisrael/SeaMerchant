package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.Display;
import android.view.View;
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

    public void initSailRoute() {
        calculateMapSize();
        putObjectOnMap(R.id.destination,
                mLogic.mSail.mDestination.toLocationX(),
                mLogic.mSail.mDestination.toLocationY(),
                0.05f, 0.10f);

        mProgress = 0.0f;

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
            float locationX = mProgress * mLogic.mSail.mDestination.toLocationX() +
                    (1.0f- mProgress) * mLogic.mSail.mSource.toLocationX();

            float locationY = mProgress * mLogic.mSail.mDestination.toLocationY() +
                    (1.0f- mProgress) * mLogic.mSail.mSource.toLocationY();

            putObjectOnMap(R.id.circle_on_map,
                    locationX,
                    locationY,
                    0.05f, 0.05f);

            mProgress += 0.02f;

            if (mProgress > 1.0f) {
                mProgress = 0.0f;
            }
        }
    };
}
