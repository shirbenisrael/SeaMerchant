package com.shirbi.seamerchant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import java.util.Timer;
import java.util.TimerTask;

public class FrontEndSail extends FrontEndGeneric {
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Point mMapSize;
    private int mProgress;
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

    private void updateGreeceSailDuration() {
        putSquareObjectOnMap(findViewById(R.id.sail_duration_for_greece),
                0.27f,
                0.32f,
                0.045f);
        findViewById(R.id.sail_duration_for_greece).setVisibility(View.VISIBLE);
    }

    private void updateIslamicSailDuration() { // Turkey and Egypt direct route
        putSquareObjectOnMap(findViewById(R.id.sail_duration_for_islamic),
                0.56f,
                0.17f,
                0.045f);
        findViewById(R.id.sail_duration_for_islamic).setVisibility(View.VISIBLE);
    }

    public void initSailRoute() {
        findViewById(R.id.sail_or_cancel_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.sail_map).setBackgroundResource(R.drawable.map_with_details);
        findViewById(R.id.destination).setVisibility(View.VISIBLE);

        calculateMapSize();
        putBoatOnHarbor();
        if (mLogic.hasMedal(Medal.GREECE_VISITOR)) {
            updateGreeceSailDuration();
        }
        if (mLogic.hasMedal(Medal.ISLAMIC_STATE)) {
            updateIslamicSailDuration();
        }
        showGuardShips();
        showDangers();
        showTotalGuardShipsPrice();
        updateGuardShipButtonColor(mLogic.mSail.mSelectedNumGuardShips);
        showTextViews();

        putObjectOnRelativeLayout(findViewById(R.id.destination),
                mLogic.mSail.mDestination.toLocationX(),
                mLogic.mSail.mDestination.toLocationY(),
                0.05f, 0.10f,
                mMapSize);

        mProgress = 0;
        mImageToAnimate = R.id.circle_on_map;
        mRealSail = false;
        findViewById(R.id.circle_on_map).setVisibility(View.VISIBLE);

        startTimer();

        setDangersIconEnable(true);
        setGuardsIconEnable(true);
    }

    Runnable mTimerTick = new Runnable() {
        public void run() {
            float startX, startY;
            float endX, endY;
            float locationX, locationY;
            float actualProgress;
            
            if (mRealSail) {
                float sailDuration = mLogic.getSailDuration(mLogic.mCurrentState, mLogic.mSail.mDestination);
                float timePassed = mProgress * sailDuration / 100;

                int timePassedHours =(int)timePassed;
                int timePassedMinutes = (int)((timePassed - timePassedHours) * 60);

                TextView landingHourTextView = findViewById(R.id.landing_hour);
                String currentTime = mActivity.getString(R.string.SAIL_CURRENT_TIME,
                        timePassedHours + mLogic.mCurrentHour,  timePassedMinutes);
                landingHourTextView.setText(currentTime);
            }

            if (mProgress < 50) {
                startX = mLogic.mSail.mSource.toLocationX();
                startY = mLogic.mSail.mSource.toLocationY();
                endX = mLogic.mSail.mSource.toMiddlePointX(mLogic.mSail.mDestination);
                endY = mLogic.mSail.mSource.toMiddlePointY(mLogic.mSail.mDestination);
                actualProgress = mProgress;
            } else {
                startX = mLogic.mSail.mSource.toMiddlePointX(mLogic.mSail.mDestination);
                startY = mLogic.mSail.mSource.toMiddlePointY(mLogic.mSail.mDestination);
                endX = mLogic.mSail.mDestination.toLocationX();
                endY = mLogic.mSail.mDestination.toLocationY();
                actualProgress = mProgress - 50;
            }
            actualProgress /= 50;

            locationX = actualProgress * endX + (1.0f - actualProgress) * startX;
            locationY = actualProgress * endY + (1.0f - actualProgress) * startY;

            putSquareObjectOnMap(findViewById(mImageToAnimate),
                    locationX,
                    locationY,
                    0.05f);

            // slow down long sail
            mProgress += (mLogic.getSailDuration(mLogic.mSail.mDestination) > 3) ? 1 : 2;

            if ((mProgress == 50) && mRealSail) {
                if (mActivity.mIsStartTutorialActive) {
                    mActivity.mStartTutorial.onMiddleSail();
                } else if (mLogic.mSail.isSinkInSail()) {
                    pauseSail();
                    mActivity.showSink();
                } else if (mLogic.mSail.isMoreDamageInSail()) {
                    pauseSail();
                    mActivity.showMoreDamage();
                } else if (mLogic.mSail.isShoalInSail()) {
                    pauseSail();
                    mActivity.showShoal();
                } else if (mLogic.mSail.isBadWeatherInSail()) {
                    pauseSail();
                    mActivity.showBadWeatherInSail();
                } else if (mLogic.mSail.isAbandonedShipAppear()) {
                    pauseSail();
                    mActivity.showAbandonedShip();
                } else if (mLogic.mSail.isPirateAppear()) {
                    pauseSail();
                    mActivity.showPirates();
                }
            }

            if (mProgress > 100) {
                if (mRealSail) {
                    sailEnd();
                    if (mLogic.mSail.isFogInSail()) {
                        pauseSail();
                        mActivity.showBadWeatherInSail();
                    } else {
                        mRealSail = false;
                        pauseSail();
                    }
                }
                mProgress = 0;
            }
        }
    };

    public void pauseSail() {
        mTimer.cancel();
        mTimer = null;
        mTimerTask.cancel();
    }

    private void setGuardsIconEnable(boolean isEnable) {
        LinearLayout guardsLayout = findViewById(R.id.guards_layout);
        int count = guardsLayout.getChildCount();
        for(int i = 0 ; i < count; i++) {
            guardsLayout.getChildAt(i).setEnabled(isEnable);
        }
    }

    private void setDangersIconEnable(boolean isEnable) {
        LinearLayout layout = ((LinearLayout)findViewById(R.id.danger_icons_layout));
        int count = layout.getChildCount();
        for(int i = 0 ; i < count; i++) {
            layout.getChildAt(i).setEnabled(isEnable);
        }
    }

    public void startSail() {
        findViewById(R.id.circle_on_map).setVisibility(View.GONE);
        findViewById(R.id.sail_or_cancel_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.sail_map).setBackgroundResource(R.drawable.map_clean);
        findViewById(R.id.destination).setVisibility(View.INVISIBLE);
        findViewById(R.id.sail_duration_for_greece).setVisibility(View.INVISIBLE);
        setDangersIconEnable(false);
        setGuardsIconEnable(false);

        mProgress = 0;
        mImageToAnimate = R.id.boat_on_map;
        mRealSail = true;
    }

    private void sailEnd() {
        mLogic.finishSail();
        mActivity.mFrontEnd.showWindow(Window.SAIL_END_WINDOW);

        String stateString = getString(mLogic.mCurrentState.toStringId());
        @StringRes int stringId = mLogic.mSail.mSailEndedPeacefully ? R.string.SAIL_ENDED_PEACEFULLY :
                R.string.SAIL_ENDED_BAD;
        String endMessage = mActivity.getString(stringId, stateString);
        TextView textView = findViewById(R.id.sail_end_message);
        textView.setText(endMessage);
        int soundId = mLogic.mSail.mSailEndedPeacefully ? R.raw.finish_sail_good : R.raw.finish_sail_bad;
        mActivity.playSound(soundId);
    }

    private void showDangers() {
        Sail sail = mLogic.mSail;

        int dangerCount = 1;

        findViewById(R.id.danger_night).setVisibility(sail.mNightSail ? View.VISIBLE : View.GONE);
        if (sail.mNightSail) {
            dangerCount++;
        }

        findViewById(R.id.danger_weather).setVisibility(sail.mSailWeather != Weather.GOOD_SAILING ?
                View.VISIBLE : View.GONE);
        findViewById(R.id.danger_weather).setBackgroundResource(sail.mSailWeather.toSmallIcon());
        if (sail.mSailWeather != Weather.GOOD_SAILING) {
            dangerCount++;
        }

        findViewById(R.id.danger_broken_ship).setVisibility(sail.mBrokenShip ? View.VISIBLE : View.GONE);
        if (sail.mBrokenShip) {
            dangerCount++;
        }

        findViewById(R.id.danger_weight).setVisibility(sail.mTooLoaded ? View.VISIBLE : View.GONE);
        if (sail.mTooLoaded) {
            dangerCount++;
        }

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                (5.0f - dangerCount) / 2.0f
        );
        findViewById(R.id.danger_padding_end).setLayoutParams(param);
        findViewById(R.id.danger_padding_start).setLayoutParams(param);
    }

    private void showTextViews() {
        Sail sail = mLogic.mSail;

        TextView guardsPriceTextView = findViewById(R.id.guard_price);
        String costString = mActivity.getString(R.string.ONE_GUARD_COST, mDecimalFormat.format(sail.mGuardShipCost), sail.mGuardShipCostPercent);
        guardsPriceTextView.setText(costString);

        TextView sailDestinationTextView = findViewById(R.id.sail_destination);
        String destinationString = mActivity.getString(R.string.SAIL_DESTINATION,
                getString(sail.mDestination.toStringId()));
        sailDestinationTextView.setText(destinationString);

        TextView landingHourTextView = findViewById(R.id.landing_hour);
        String landingHour= mActivity.getString(R.string.SAIL_LANDING_HOUR, sail.mLandingHour);
        landingHourTextView.setText(landingHour);
    }

    public void showGuardShips() {
        Sail sail = mLogic.mSail;

        LinearLayout guardsLayout = findViewById(R.id.guards_layout);
        for (int i = 1; i <= sail.MAX_GUARD_SHIPS; i++) {
            Button button = (Button)guardsLayout.getChildAt(i);
            button.setVisibility(i <= sail.mMaxGuardShips ? View.VISIBLE : View.INVISIBLE);
            button.setText(String.valueOf(i));
        }
        Button button = (Button)guardsLayout.getChildAt(0);
        button.setText(String.valueOf(0));
        button.setBackgroundResource(R.drawable.guard_ship_gray);
    }

    private void showTotalGuardShipsPrice() {
        String guardCostMessage =
                mActivity.getString(R.string.TOTAL_GUARD_COST, mDecimalFormat.format(mLogic.mSail.mTotalGuardShipsCost),
                        mDecimalFormat.format(mLogic.mCash));
        ((TextView)findViewById(R.id.total_guard_cost)).setText(guardCostMessage);
    }

    private void updateGuardShipButtonColor(int numGuards) {
        LinearLayout guardsLayout = findViewById(R.id.guards_layout);
        for (int i = 1; i <= numGuards; i++) {
            Button button = (Button) guardsLayout.getChildAt(i);
            button.setBackgroundResource(R.drawable.guard_ship);
        }
        for (int i = numGuards + 1; i <= Sail.MAX_GUARD_SHIPS; i++) {
            Button button = (Button) guardsLayout.getChildAt(i);
            button.setBackgroundResource(R.drawable.guard_ship_gray);
        }
    }

    public void guardShipClick(View view) {
        TextView textView = (TextView)view;
        int numGuards = Integer.parseInt(textView.getText().toString());
        mLogic.mSail.selectNumGuardShips(numGuards);
        showTotalGuardShipsPrice();
        updateGuardShipButtonColor(numGuards);
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

        int period = mActivity.mIsFastAnimation ? 33 : 100;
        mTimer.schedule(mTimerTask,0, period);
    }

    public void continueSail() {
        startTimer();
    }

    public void showAlertWeatherMessage() {
        String message;
        switch (mLogic.mWeather) {
            case WIND:
                message = getString(R.string.DANGER_WIND_MESSAGE);
                break;
            case FOG:
                message = getString(R.string.DANGER_FOG_SHORT_MESSAGE);
                break;
            case STORM:
                message = getString(R.string.DANGER_STORM_MESSAGE);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mLogic.mWeather);
        }

        showAlertDialogMessage(message, getString(R.string.DANGER_WEATHER_TITLE));
    }

    public void showAllButtons() {
        findViewById(R.id.danger_and_text_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.guard_price).setVisibility(View.VISIBLE);
        findViewById(R.id.guards_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.total_guard_cost).setVisibility(View.VISIBLE);
    }

    public void showOnlyStartSail() {
        findViewById(R.id.danger_and_text_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.guard_price).setVisibility(View.INVISIBLE);
        findViewById(R.id.guards_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.total_guard_cost).setVisibility(View.INVISIBLE);
    }

    void showSailWithNoGuardsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(getString(R.string.SAIL_WITH_NO_GURADS));
        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActivity.startSail();
            }
        });
        builder.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setIcon(R.drawable.pirates_icon);
        builder.show();
    }
}
