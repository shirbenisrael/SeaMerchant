package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class FrontEnd extends FrontEndGeneric {
    private Point mFlagSize;

    public FrontEnd(MainActivity activity) {
        super(activity);

        LinearLayout goodsLayout = findViewById(R.id.goods_buttons);
        for (Goods goods: Goods.values()) {
            Button imageButton = (Button)goodsLayout.getChildAt(goods.getValue());
            imageButton.setBackgroundResource(goods.toWideButtonId());
        }

        calculateFlagSize();

        LinearLayout statesLayout = findViewById(R.id.prices_layout);
        for (State state : State.values()) {
            LinearLayout stateLayout = (LinearLayout)statesLayout.getChildAt(state.getValue());

            RelativeLayout flagButtonWrapper = (RelativeLayout)stateLayout.getChildAt(0);
            Button flagButton = (Button)flagButtonWrapper.getChildAt(0);
            flagButton.setBackgroundResource(state.toFlagId());

            ImageView flagSailDuration = (ImageView)flagButtonWrapper.getChildAt(1);
            putObjectOnRelativeLayout(flagSailDuration, 0, 0, 0.33f, 0.5f, mFlagSize );

            ImageView flagWeather = (ImageView)flagButtonWrapper.getChildAt(2);
            putObjectOnRelativeLayout(flagWeather, 0, 0.5f, 0.33f, 0.5f, mFlagSize );
        }
    }

    private void showInventory() {
        LinearLayout goodsLayout = findViewById(R.id.goods_buttons);

        for (Goods goods: Goods.values()) {
            Button goodsButton = (Button)goodsLayout.getChildAt(goods.getValue());
            goodsButton.setText(String.valueOf(mLogic.mInventory[goods.getValue()]));
        }
    }

    private void showPrices(PriceTable priceTable) {
        LinearLayout statesLayout = findViewById(R.id.prices_layout);
        for (State state : State.values()) {
            LinearLayout stateLayout = (LinearLayout)statesLayout.getChildAt(state.getValue());

            if (state == mLogic.mCurrentState) {
                stateLayout.setBackgroundColor(mActivity.getColor(R.color.transparent_red));
            } else {
                stateLayout.setBackgroundColor(mActivity.getColor(R.color.transparent));
            }

            for (Goods goods: Goods.values()) {
                TextView textView = (TextView)stateLayout.getChildAt(goods.getValue() + 1);
                textView.setText(String.valueOf(priceTable.getPrice(state, goods)));
            }
        }
    }

    public void showState() {
        showPrices(mLogic.mPriceTable);
        showInventory();

        TextView textView;
        Button button;

        textView = findViewById(R.id.current_hour_text_view);
        textView.setText(String.valueOf(mLogic.mCurrentHour) + ":00");

        textView = findViewById(R.id.current_day_text_view);
        textView.setText(getString(mLogic.mCurrentDay.toStringId()));

        textView = findViewById(R.id.current_state_text_view);
        textView.setText(getString(mLogic.mCurrentState.toStringId()));

        textView = findViewById(R.id.current_weather_text_view);
        textView.setText(generateWeatherString());

        textView = findViewById(R.id.wide_capacity_button);
        textView.setText(String.valueOf(mLogic.mCapacity) + " " + getString(R.string.TONNE));

        button = findViewById(R.id.wide_fix_button);
        @DrawableRes int resource = (mLogic.mDamage == 0) ? R.drawable.wide_ship_button : R.drawable.wide_fix_button;
        button.setBackgroundResource(resource);
        String damageString = (mLogic.mDamage == 0) ? getString(R.string.FIXED_SHIP) :
                mActivity.getString(R.string.DAMAGED_SHIP, mLogic.mDamage);
        button.setText(damageString);

        textView = findViewById(R.id.wide_cash_button);
        textView.setText(mActivity.getString(R.string.MONEY_STRING, mLogic.mCash));

        textView = findViewById(R.id.wide_bank_button);
        textView.setText(mActivity.getString(R.string.MONEY_STRING, mLogic.mBankDeposit));

        findViewById(R.id.main_window_layout).setBackgroundResource(mLogic.getDayPart().toImageId());

        showWeatherOnFlag();
        showSailDurationOnFlag();
    }

    public Goods viewToGoods(View view) {
        LinearLayout goodsLayout = findViewById(R.id.goods_buttons);
        for (Goods goods: Goods.values()) {
            if (goodsLayout.getChildAt(goods.getValue()) == view) {
                return goods;
            }
        }
        return null;
    }

    public State viewToState(View view) {
        LinearLayout priceLayout = findViewById(R.id.prices_layout);
        for (int i = 0; i < State.NUM_STATES; i ++) {
            LinearLayout stateLayout = (LinearLayout)priceLayout.getChildAt(i);

            RelativeLayout flagButtonWrapper = (RelativeLayout)stateLayout.getChildAt(0);
            if (flagButtonWrapper.getChildAt(0) == view) {
                return State.values()[i];
            }
        }
        return null;
    }

    public void showWindow(Window windowToShow) {
        for (Window window : Window.values()) {
            if (window == windowToShow) {
                findViewById(window.toLayoutId()).setVisibility(View.VISIBLE);
            } else {
                findViewById(window.toLayoutId()).setVisibility(View.GONE);
            }
        }
    }

    private @NonNull String generateWeatherString() {
        Weather weather = mLogic.mWeather;
        String weatherString;
        findViewById(Window.WEATHER_WINDOW.toLayoutId()).setBackgroundResource(weather.toBackground());
        if (weather == Weather.GOOD_SAILING) {
            weatherString = mActivity.getString(weather.toStringId());
        } else {
            weatherString = mActivity.getString(weather.toStringId(), getString(mLogic.mWeatherState.toStringId()));
        }

        return weatherString;
    }

    public void showNewWeather() {
        ((TextView)findViewById(R.id.day_message)).setText(mLogic.mCurrentDay.toStringId());
        ((TextView)findViewById(R.id.weather_message)).setText(generateWeatherString());
    }

    private void calculateFlagSize() {
        mFlagSize = getWindowSize(); // need to adjust y

        float weightY = 1.0f;

        LinearLayout main_window_layout = findViewById(Window.MAIN_WINDOW.toLayoutId());
        main_window_layout = (LinearLayout)main_window_layout.getChildAt(0);

        float totalWeightsY = 0;
        for (int i = 0 ; i < main_window_layout.getChildCount(); i++) {
            View view = main_window_layout.getChildAt(i);
            totalWeightsY += ((LinearLayout.LayoutParams)view.getLayoutParams()).weight;
        }

        mFlagSize.y *= weightY / totalWeightsY;

        mFlagSize.x /= 4; // Flags sit near 3 prices.
    }

    private void showWeatherOnFlag() {
        LinearLayout statesLayout = findViewById(R.id.prices_layout);
        for (State state : State.values()) {
            LinearLayout stateLayout = (LinearLayout)statesLayout.getChildAt(state.getValue());

            RelativeLayout flagButtonWrapper = (RelativeLayout)stateLayout.getChildAt(0);
            ImageView flagWeather = (ImageView)flagButtonWrapper.getChildAt(2);
            if (state == mLogic.mWeatherState && mLogic.mWeather != Weather.GOOD_SAILING) {
                flagWeather.setVisibility(View.VISIBLE);
                flagWeather.setImageResource(mLogic.mWeather.toBackground());
            } else {
                flagWeather.setVisibility(View.GONE);
            }
        }
    }

    private void showSailDurationOnFlag() {
        LinearLayout statesLayout = findViewById(R.id.prices_layout);
        for (State state : State.values()) {
            LinearLayout stateLayout = (LinearLayout)statesLayout.getChildAt(state.getValue());

            RelativeLayout flagButtonWrapper = (RelativeLayout)stateLayout.getChildAt(0);
            ImageView flagSailDuration = (ImageView)flagButtonWrapper.getChildAt(1);

            int sail_duration = mLogic.getSailDuration(state);
            switch (sail_duration) {
                case 3:
                    flagSailDuration.setImageResource(R.drawable.duration_3);
                    break;
                case 5:
                    flagSailDuration.setImageResource(R.drawable.duration_5);
                    break;
                case 6:
                    flagSailDuration.setImageResource(R.drawable.duration_6);
                    break;
                default:
                    flagSailDuration.setImageResource(R.drawable.duration_invalid);
            }
        }
    }

    private @NonNull String generateNewDaySpecialPriceString() {
        @StringRes int id = mLogic.mIsSpecialPriceHigh ? R.string.PRICE_UP : R.string.PRICE_DOWN;
        State state = mLogic.mSpecialPriceState;
        Goods goods = mLogic.mSpecialPriceGoods;
        int newPrice = mLogic.mPriceTable.getPrice(state, goods);
        String goodsString = getString(goods.toStringId());
        String stateString = getString(state.toStringId());
        String eventString = mActivity.getString(id, goodsString, stateString, newPrice);
        return eventString;
    }

    public void showNewEvent() {
        @DrawableRes int backgroundId;
        String message;
        ((TextView)findViewById(R.id.day_message_with_event)).setText(mLogic.mCurrentDay.toStringId());
        switch (mLogic.mNewDayEvent) {
            case FISH_BOAT_COLLISION:
                backgroundId = R.drawable.fish_boat;
                message = mActivity.getString(R.string.FISH_BOAT, mLogic.mFishBoatCollisionDamage);
                break;

            case SPECIAL_PRICE:
                backgroundId = mLogic.mIsSpecialPriceHigh ? R.drawable.price_up : R.drawable.price_down;
                message = generateNewDaySpecialPriceString();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mLogic.mNewDayEvent);
        }

        ((TextView)findViewById(R.id.special_event_message)).setText(message);
        findViewById(R.id.simple_new_day_event_layout).setBackgroundResource(backgroundId);
    }
}
