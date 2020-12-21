package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class FrontEnd extends FrontEndGeneric {
    private Point mFlagSize;
    FrontEndTimer mFrontEndMarketBlinkTimer;
    FrontEndTimer mFrontEndSailBlinkTimer;

    public FrontEnd(MainActivity activity) {
        super(activity);
        mFrontEndMarketBlinkTimer = new FrontEndTimer(this);
        mFrontEndSailBlinkTimer = new FrontEndTimer(this);

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

        button = findViewById(R.id.wide_sleep_button);
        @DrawableRes int sleepButtonResource = (mLogic.mCurrentDay.isLastDay())
                ? R.drawable.wide_end_game_button : R.drawable.wide_sleep_button;
        button.setBackgroundResource(sleepButtonResource);

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

    private  @NonNull  String generateNewDayMerchantOffer() {
        String offerString;
        String goodsString = getString(mLogic.mMerchantGoods.toStringId());
        if (mLogic.mIsMerchantBuy) {
            offerString = mActivity.getString(R.string.MERCHANT_BUY, goodsString, mLogic.mMerchantPrice);
        } else {
            offerString = mActivity.getString(R.string.MERCHANT_SELL, goodsString,
                    mLogic.mMerchantUnits, mLogic.mMerchantPrice);
        }

        return offerString;
    }

    private  @NonNull  String generateNewDayBiggerShipOffer() {
        String offerString;
        if (mLogic.mIsBiggerShipForCash) {
            offerString = mActivity.getString(R.string.BIGGER_SHIP_FOR_CASH_OFFER,
                    mLogic.mBiggerShipCapacity, mLogic.mBiggerShipPrice);
        } else {
            String goodsString = getString(mLogic.mBiggerShipPriceGoodType.toStringId());
            offerString = mActivity.getString(R.string.BIGGER_SHIP_FOR_GOODS_OFFER,
                    mLogic.mBiggerShipCapacity, mLogic.mBiggerShipPrice, goodsString);
        }

        return offerString;
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
        @IdRes int idToShow;
        @IdRes int idToHide;
        int soundId = 0;
        ((TextView)findViewById(R.id.day_message_with_event)).setText(mLogic.mCurrentDay.toStringId());
        switch (mLogic.mNewDayEvent) {
            case MERCHANT:
                idToShow = R.id.agree_or_cancel_layout;
                idToHide = R.id.approve_event;
                backgroundId = R.drawable.merchant;
                message = generateNewDayMerchantOffer();
                soundId = R.raw.special_offer;
                break;
            case BIGGER_SHIP_OFFER:
                idToShow = R.id.agree_or_cancel_layout;
                idToHide = R.id.approve_event;
                backgroundId = R.drawable.bigger_ship;
                message = generateNewDayBiggerShipOffer();
                soundId = R.raw.special_offer;
                break;

            case FIRE:
                idToShow = R.id.approve_event;
                idToHide = R.id.agree_or_cancel_layout;
                backgroundId = R.drawable.fire;
                String goodsString = getString(mLogic.mGoodsToBurn.toStringId());
                message = mActivity.getString(R.string.FIRE, mLogic.mGoodsUnitsToBurn, goodsString);
                soundId = R.raw.fire;
                break;

            case FISH_BOAT_COLLISION:
                idToShow = R.id.approve_event;
                idToHide = R.id.agree_or_cancel_layout;
                backgroundId = R.drawable.fish_boat;
                message = mActivity.getString(R.string.FISH_BOAT, mLogic.mFishBoatCollisionDamage);
                soundId = R.raw.fish_boat;
                break;

            case SPECIAL_PRICE:
                idToShow = R.id.approve_event;
                idToHide = R.id.agree_or_cancel_layout;
                backgroundId = mLogic.mIsSpecialPriceHigh ? R.drawable.price_up : R.drawable.price_down;
                message = generateNewDaySpecialPriceString();
                soundId = mLogic.mIsSpecialPriceHigh ? R.raw.up : R.raw.down;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mLogic.mNewDayEvent);
        }

        ((TextView)findViewById(R.id.special_event_message)).setText(message);
        findViewById(R.id.simple_new_day_event_layout).setBackgroundResource(backgroundId);
        findViewById(idToShow).setVisibility(View.VISIBLE);
        findViewById(idToHide).setVisibility(View.GONE);
        mActivity.playSound(soundId);
    }

    private void showCrewNegotiationResult(@StringRes int part1, @StringRes int part2 ) {
        findViewById(R.id.approve_event).setVisibility(View.VISIBLE);
        findViewById(R.id.agree_or_cancel_layout).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.day_message_with_event)).setText(part1);
        ((TextView)findViewById(R.id.special_event_message)).setText(part2);
    }

    public void showCrewNegotiationFail() {
        findViewById(R.id.simple_new_day_event_layout).setBackgroundResource(R.drawable.booing);
        showCrewNegotiationResult(R.string.CREW_REFUSE_OFFER, R.string.LOSE_2_DAYS);
    }

    public void showCrewNegotiationSucceed() {
        findViewById(R.id.simple_new_day_event_layout).setBackgroundResource(R.drawable.accept_offer);
        showCrewNegotiationResult(mLogic.mCurrentDay.toStringId(), R.string.CREW_OFFER_ACCEPTED);
    }

    public void showNewCrewNextDay() {
        findViewById(R.id.simple_new_day_event_layout).setBackgroundResource(R.drawable.strike);
        showCrewNegotiationResult(mLogic.mCurrentDay.toStringId(), R.string.NEXT_DAY_NEW_CREW);
    }

    public void showSailWarning() {
        if (mLogic.mSail.isWarningRelevant()) {
            showWindow(Window.DANGER_WINDOW);

            @DrawableRes int backgroundId;
            @ColorInt int color;
            String message;
            switch (mLogic.mSail.mWarning) {
                case DAMAGED_SHIP:
                    backgroundId = R.drawable.broken_ship;
                    message = getString(R.string.DANGER_BROKEN_SHIP_MESSAGE);
                    color = mActivity.getColor(R.color.yellow);
                    break;
                case OVERLOAD:
                    backgroundId = R.drawable.overload;
                    message = getString(R.string.DANGER_OVERLOAD_MESSAGE);
                    color = mActivity.getColor(R.color.black);
                    break;
                case NIGHT_SAIL:
                    backgroundId = R.drawable.night_sail;
                    message = mActivity.getString(R.string.DANGER_NIGHT_SAIL_MESSAGE,
                            mLogic.mSail.calculateMaxShoalDamage());
                    color = mActivity.getColor(R.color.yellow);
                    break;
                case WEATHER:
                    backgroundId = mLogic.mWeather.toBackground();
                    switch (mLogic.mWeather) {
                        case FOG:
                            message = mActivity.getString(R.string.DANGER_FOG_MESSAGE, mLogic.mWeatherState);
                            break;
                        case STORM:
                            message = getString(R.string.DANGER_STORM_MESSAGE);
                            break;
                        case WIND:
                            message = getString(R.string.DANGER_WIND_MESSAGE);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + mLogic.mWeather);
                    }
                    color = mActivity.getColor(R.color.black);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + mLogic.mSail.mWarning);
            }
            findViewById(R.id.danger_layout).setBackgroundResource(backgroundId);
            ((TextView)findViewById(R.id.danger_title)).setText(mLogic.mSail.mWarning.toTitleStringId());
            ((TextView)findViewById(R.id.danger_message)).setText(message);
            ((TextView)findViewById(R.id.danger_title)).setTextColor(color);
            ((TextView)findViewById(R.id.danger_message)).setTextColor(color);
        } else {
            mActivity.onApproveDanger(null);
        }
    }

    public void showTutorial() {
        if (mLogic.mTutorial.isSuggestToSell()) {
            Goods goods = mLogic.mTutorial.mGoodsToDeal;
            State state = mLogic.mCurrentState;
            String goodsString = getString(goods.toStringId());
            String stateString = getString(state.toStringId());
            int price = mLogic.mPriceTable.getPrice(state, goods);
            int units = mLogic.getInventory(goods);

            String message = mActivity.getString(R.string.TUTORIAL_SELL, stateString, goodsString, price, units);
            showAlertDialogMessage(message, getString(R.string.TUTORIAL_TITLE));
            blinkMarket();
            return;
        }

        if (mLogic.mTutorial.isSuggestToBuy()) {
            Goods goods = mLogic.mTutorial.mGoodsToDeal;
            State state = mLogic.mCurrentState;
            State otherState = mLogic.mTutorial.mStateToSail;
            String goodsString = getString(goods.toStringId());
            String stateString = getString(state.toStringId());
            String otherStateString = getString(otherState.toStringId());
            int price = mLogic.mPriceTable.getPrice(state, goods);
            int otherPrice = mLogic.mPriceTable.getPrice(otherState, goods);

            String message = mActivity.getString(R.string.TUTORIAL_BUY, stateString, goodsString, price, otherStateString, otherPrice);
            showAlertDialogMessage(message, getString(R.string.TUTORIAL_TITLE));
            blinkMarket();
            return;
        }

        if (mLogic.mTutorial.isSuggestToSail()) {
            if (mLogic.mTutorial.mStateToSail == null) {
                State state = mLogic.mCurrentState;
                String stateString = getString(state.toStringId());

                String message = mActivity.getString(R.string.TUTORIAL_SAIL_EMPTY, stateString);
                showAlertDialogMessage(message, getString(R.string.TUTORIAL_TITLE));
            } else {
                Goods goods = mLogic.mTutorial.mGoodsToDeal;
                State otherState = mLogic.mTutorial.mStateToSail;
                String goodsString = getString(goods.toStringId());
                String otherStateString = getString(otherState.toStringId());
                int otherPrice = mLogic.mPriceTable.getPrice(otherState, goods);

                String message = mActivity.getString(R.string.TUTORIAL_SAIL_WITH_GOODS, goodsString, otherStateString, otherPrice);
                showAlertDialogMessage(message, getString(R.string.TUTORIAL_TITLE));
            }
            blinkSail();
            return;
        }

        showAlertDialogMessage(getString(R.string.TUTORIAL_SLEEP), getString(R.string.TUTORIAL_TITLE));
    }

    private void blinkInventory(boolean isRed) {
        LinearLayout goodsLayout = findViewById(R.id.goods_buttons);
        int colorId = isRed ? R.color.red : R.color.black;
        @ColorInt int color = mActivity.getColor(colorId);

        for (Goods goods: Goods.values()) {
            Button goodsButton = (Button)goodsLayout.getChildAt(goods.getValue());
            goodsButton.setTextColor(color);
        }

        @DrawableRes int backGroundId = isRed ? R.drawable.market_help_red : R.drawable.market_help;
        findViewById(R.id.market_help).setBackgroundResource(backGroundId);
    }

    private void blinkFlags(boolean isRed) {
        @DrawableRes int backGroundId = isRed ? R.drawable.sail_help_red : R.drawable.sail_help;
        findViewById(R.id.sail_help).setBackgroundResource(backGroundId);
    }

    public void timerBlinked(FrontEndTimer timer, int countDown) {
        if (timer == mFrontEndMarketBlinkTimer) {
            blinkInventory(countDown % 2 != 0);
        } else if (timer == mFrontEndSailBlinkTimer) {
            blinkFlags(countDown % 2 != 0);
        }
    }

    public void blinkMarket() {
        mFrontEndMarketBlinkTimer.startTimer(500, 20);
    }

    public void blinkSail() {
        mFrontEndSailBlinkTimer.startTimer(500, 20);
    }

    public void showSleepQuestion() {
        ((TextView)findViewById(R.id.sleep_message)).setText(mLogic.mCurrentDay.isLastDay() ?
                R.string.END_GAME_QUESTION:R.string.SLEEP_QUESTION);
    }
}
