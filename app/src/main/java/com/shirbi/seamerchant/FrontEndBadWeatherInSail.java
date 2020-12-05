package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndBadWeatherInSail extends FrontEndGeneric {

    public FrontEndBadWeatherInSail(MainActivity activity) {
        super(activity);
    }

    public void showBadWeather() {
        Sail sail = mLogic.mSail;
        findViewById(R.id.bad_weather_in_sail_layout).setBackgroundResource(R.drawable.navigation_error);

        ((TextView)findViewById(R.id.bad_weather_title)).setText(getString(R.string.NAVIGATION_ERROR_TITLE));

        String newDestination = getString(sail.mDestination.toStringId());
        String newSource = getString(sail.mSource.toStringId());
        String result = mActivity.getString(R.string.NAVIGATION_ERROR_RESULT, newDestination, newSource);
        ((TextView)findViewById(R.id.bad_weather_result)).setText(result);
    }
}
