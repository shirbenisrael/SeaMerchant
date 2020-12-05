package com.shirbi.seamerchant;

import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class FrontEndBadWeatherInSail extends FrontEndGeneric {

    public FrontEndBadWeatherInSail(MainActivity activity) {
        super(activity);
    }

    public void showBadWeather() {
        Sail sail = mLogic.mSail;
        String result;
        String newDestination;
        @DrawableRes int backGroundId;
        @StringRes int titleId;

        switch (mLogic.mWeather) {
            case GOOD_SAILING:
                return;

            case WIND:
                backGroundId = (R.drawable.navigation_error);
                titleId = R.string.NAVIGATION_ERROR_TITLE;

                newDestination = getString(sail.mDestination.toStringId());
                String newSource = getString(sail.mSource.toStringId());
                result = mActivity.getString(R.string.NAVIGATION_ERROR_RESULT, newDestination, newSource);
                break;

            case STORM:
                titleId = R.string.STORM_TITLE;
                if (sail.mIsStormWashGoods) {
                    backGroundId = (R.drawable.storm_with_lost_goods);
                    findViewById(R.id.bad_weather_in_sail_layout).setBackgroundResource(R.drawable.storm_with_lost_goods);
                    String lostGoodsType = getString(sail.mStormLostGoodType.toStringId());
                    result = mActivity.getString(R.string.STORM_WASH_GOODS, sail.mStormLostUnits, lostGoodsType);
                } else {
                    backGroundId = (R.drawable.storm_with_damage);
                    findViewById(R.id.bad_weather_in_sail_layout).setBackgroundResource(R.drawable.storm_with_damage);
                    result = mActivity.getString(R.string.STORM_HURT_SHIP, sail.mStormLostUnits);
                }
                break;

            case FOG:
                backGroundId = (R.drawable.fog_weather);
                titleId = R.string.FOG_TITLE;
                ((TextView) findViewById(R.id.bad_weather_title)).setText(getString(R.string.FOG_TITLE));

                newDestination = getString(sail.mDestination.toStringId());
                result = mActivity.getString(R.string.FOG_RESULT, newDestination);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mLogic.mWeather);
        }

        findViewById(R.id.bad_weather_in_sail_layout).setBackgroundResource(backGroundId);
        ((TextView) findViewById(R.id.bad_weather_title)).setText(titleId);
        ((TextView) findViewById(R.id.bad_weather_result)).setText(result);
    }
}
