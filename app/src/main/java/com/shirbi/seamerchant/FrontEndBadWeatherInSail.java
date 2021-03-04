package com.shirbi.seamerchant;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class FrontEndBadWeatherInSail extends FrontEndGeneric {

    public FrontEndBadWeatherInSail(MainActivity activity) {
        super(activity);
    }

    private void showNextStateButtonsInFog() {
        LinearLayout statesLayout = findViewById(R.id.next_state_after_fog_layout);

        for (State state: State.values()) {
            Button stateButton = (Button) statesLayout.getChildAt(state.getValue());

            // source and destination where already swapped.
            int newDuration = mLogic.getSailDuration(mLogic.mSail.mSource, state);
            int oldDuration = mLogic.getSailDuration(mLogic.mSail.mSource, mLogic.mSail.mDestination);

            if (newDuration > 0 && newDuration <= oldDuration) {
                stateButton.setVisibility(View.VISIBLE);
            } else {
                stateButton.setVisibility(View.GONE);
            }
        }
    }

    public void showBadWeather() {
        Sail sail = mLogic.mSail;
        String result;
        String newDestination;
        @DrawableRes int backGroundId;
        @StringRes int titleId;
        int soundId = 0;

        switch (mLogic.mWeather) {
            case GOOD_SAILING:
                return;

            case WIND:
                backGroundId = (R.drawable.navigation_error);
                titleId = R.string.NAVIGATION_ERROR_TITLE;

                newDestination = getString(sail.mDestination.toStringId());
                String newSource = getString(sail.mSource.toStringId());
                result = mActivity.getString(R.string.NAVIGATION_ERROR_RESULT, newDestination, newSource);
                soundId = R.raw.wind;
                break;

            case STORM:
                titleId = R.string.STORM_TITLE;
                if (sail.mIsStormWashGoods) {
                    backGroundId = (R.drawable.storm_with_lost_goods);
                    findViewById(R.id.bad_weather_in_sail_layout).setBackgroundResource(R.drawable.storm_with_lost_goods);
                    String lostGoodsType = getGoodsString(sail.mStormLostGoodType);
                    result = mActivity.getString(R.string.STORM_WASH_GOODS, mDecimalFormat.format(sail.mStormLostUnits), lostGoodsType);
                } else {
                    backGroundId = (R.drawable.storm_with_damage);
                    findViewById(R.id.bad_weather_in_sail_layout).setBackgroundResource(R.drawable.storm_with_damage);
                    result = mActivity.getString(R.string.STORM_HURT_SHIP, mDecimalFormat.format(sail.mStormLostUnits));
                }
                soundId = R.raw.storm;
                break;

            case FOG:
                backGroundId = (R.drawable.fog_weather);
                titleId = R.string.FOG_TITLE;
                ((TextView) findViewById(R.id.bad_weather_title)).setText(getString(R.string.FOG_TITLE));

                newDestination = getString(sail.mDestination.toStringId());
                result = mActivity.getString(R.string.FOG_RESULT, newDestination);
                soundId = R.raw.wind;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mLogic.mWeather);
        }

        findViewById(R.id.bad_weather_in_sail_layout).setBackgroundResource(backGroundId);
        ((TextView) findViewById(R.id.bad_weather_title)).setText(titleId);

        if (mLogic.mWeather == Weather.FOG && mLogic.hasMedal(Medal.FOG_OF_WAR)) {
            ((TextView) findViewById(R.id.bad_weather_result)).setText(getString(R.string.FOG_NEXT_STATE));
            findViewById(R.id.yes_button_after_bad_weather_in_sail).setVisibility(View.GONE);
            findViewById(R.id.next_state_after_fog_layout).setVisibility(View.VISIBLE);
            showNextStateButtonsInFog();
        } else {
            findViewById(R.id.yes_button_after_bad_weather_in_sail).setVisibility(View.VISIBLE);
            findViewById(R.id.next_state_after_fog_layout).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.bad_weather_result)).setText(result);
        }

        mActivity.playSound(soundId);
    }

    public void selectDestinationAfterFog(View newStateButton) {
        int viewIndex = ((ViewGroup)newStateButton.getParent()).indexOfChild(newStateButton);
        mLogic.mSail.setNewDestinationAfterFog(State.values()[viewIndex]);
    }
}
