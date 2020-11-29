package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.View;
import android.widget.Button;
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

    void putSquareObjectOnMap(View object, float x, float y, float size) {
        putObjectOnRelativeLayout(object,
                x, y, size, size * mMapSize.x / mMapSize.y, // square
                mMapSize);
    }

    private void putBoatOnHarbor() {
        putSquareObjectOnMap(findViewById(R.id.boat_on_map),
                mLogic.mSail.mSource.toLocationX(),
                mLogic.mSail.mSource.toLocationY(),
                0.10f);

        ImageView boat = findViewById(R.id.boat_on_map);

        if (mLogic.mSail.mSource.toLocationX() > mLogic.mSail.mDestination.toLocationX()) {
            boat.setImageResource(R.drawable.boat_left);
        } else {
            boat.setImageResource(R.drawable.boat_right);
        }
    }

    public void initSailRoute() {
        findViewById(R.id.sail_or_cancel_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.sail_map).setBackgroundResource(R.drawable.map_with_details);
        findViewById(R.id.destination).setVisibility(View.VISIBLE);

        calculateMapSize();
        putBoatOnHarbor();
        showGuardShips();

        putObjectOnRelativeLayout(findViewById(R.id.destination),
                mLogic.mSail.mDestination.toLocationX(),
                mLogic.mSail.mDestination.toLocationY(),
                0.05f, 0.10f,
                mMapSize);

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

            putSquareObjectOnMap(findViewById(mImageToAnimate),
                    locationX,
                    locationY,
                    0.05f);

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
        findViewById(R.id.sail_or_cancel_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.sail_map).setBackgroundResource(R.drawable.map_clean);
        findViewById(R.id.destination).setVisibility(View.INVISIBLE);

        mProgress = 0;
        mImageToAnimate = R.id.boat_on_map;
        mRealSail = true;
    }

    private void sailEnd() {
        mLogic.finishSail();
        mActivity.mFrontEnd.showWindow(Window.SAIL_END_WINDOW);
    }

    public void showGuardShips() {
        LinearLayout guardsLayout = findViewById(R.id.guards_layout);
        for (int i = 1; i <= mLogic.mSail.MAX_GUARD_SHIPS; i++) {
            Button button = (Button)guardsLayout.getChildAt(i);
            button.setVisibility(i <= mLogic.mSail.mMaxGuardShips ? View.VISIBLE : View.INVISIBLE);
            button.setText(String.valueOf(i));
        }
        Button button = (Button)guardsLayout.getChildAt(0);
        button.setText(String.valueOf(0));
        button.setBackgroundResource(R.drawable.guard_ship_gray);
    }
}
