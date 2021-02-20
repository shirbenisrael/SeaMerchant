package com.shirbi.seamerchant;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class FrontEnd extends FrontEndGeneric {
    private Point mFlagSize;
    FrontEndTimer mFrontEndMarketBlinkTimer;
    FrontEndTimer mFrontEndSailBlinkTimer;
    FrontEndTimer mFrontEndSleepBlinkTimer;
    FrontEndTimer mFrontEndFixShipBlinkTimer;
    FrontEndTimer mFrontEndBankBlinkTimer;

    State mStateToBlink;
    Goods mGoodsToBlink;

    FrontEndNumberAnimation mFrontEndNumberAnimationCash;
    FrontEndNumberAnimation mFrontEndNumberAnimationBank;
    FrontEndNumberAnimation mFrontEndNumberAnimationInventory[];
    FrontEndTimeAnimation mFrontEndTimeAnimation;

    public FrontEnd(MainActivity activity) {
        super(activity);
        mFrontEndMarketBlinkTimer = new FrontEndTimer(this);
        mFrontEndSailBlinkTimer = new FrontEndTimer(this);
        mFrontEndSleepBlinkTimer = new FrontEndTimer(this);
        mFrontEndFixShipBlinkTimer = new FrontEndTimer(this);
        mFrontEndBankBlinkTimer = new FrontEndTimer(this);

        mFrontEndNumberAnimationCash = new FrontEndNumberAnimation(mActivity, R.id.wide_cash_button, R.string.MONEY_STRING);
        mFrontEndNumberAnimationBank = new FrontEndNumberAnimation(mActivity, R.id.wide_bank_button, R.string.MONEY_STRING);
        mFrontEndNumberAnimationInventory = new FrontEndNumberAnimation[Goods.NUM_GOODS_TYPES];
        mFrontEndTimeAnimation = new FrontEndTimeAnimation(mActivity, R.id.current_hour_text_view);

        LinearLayout goodsLayout = findViewById(R.id.goods_buttons);
        for (Goods goods: Goods.values()) {
            Button imageButton = (Button)goodsLayout.getChildAt(goods.getValue());
            imageButton.setBackgroundResource(goods.toWideButtonId());
            mFrontEndNumberAnimationInventory[goods.getValue()] =
                    new FrontEndNumberAnimation(mActivity, imageButton, 0);
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

    public void showInventory() {
        for (Goods goods: Goods.values()) {
            mFrontEndNumberAnimationInventory[goods.getValue()].changeNumber(mLogic.mInventory[goods.getValue()]);
        }
    }

    public void showPrices() {
        PriceTable priceTable = mLogic.mPriceTable;
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
        showPrices();
        showInventory();

        TextView textView;
        Button button;

        mFrontEndTimeAnimation.updateTime(mLogic.mCurrentHour);

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

        mFrontEndNumberAnimationCash.changeNumber(mLogic.mCash);
        mFrontEndNumberAnimationBank.changeNumber(mLogic.mBankDeposit);

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

    public void showGameRules() {
        findViewById(R.id.statistics_text).setVisibility(View.GONE);
        findViewById(R.id.game_rules).setVisibility(View.VISIBLE);
    }

    public void showStats() {
        findViewById(R.id.statistics_text).setVisibility(View.VISIBLE);
        findViewById(R.id.game_rules).setVisibility(View.GONE);
    }


    public @NonNull Window getCurrentVisibleWindow() {
        for (Window window : Window.values()) {
            if (findViewById(window.toLayoutId()).getVisibility() == View.VISIBLE) {
                return window;
            }
        }

        throw new IllegalStateException("No window is visible");
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
        ((TextView)findViewById(R.id.calculator)).setText(R.string.CALCULATOR);
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
                flagWeather.setImageResource(mLogic.mWeather.toSmallIcon());
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
                    mLogic.mBiggerShipCapacity, mLogic.mBiggerShipPrice, mLogic.mCash);
        } else {
            String goodsString = getString(mLogic.mBiggerShipPriceGoodType.toStringId());
            int inventory = mLogic.getInventory(mLogic.mBiggerShipPriceGoodType);
            offerString = mActivity.getString(R.string.BIGGER_SHIP_FOR_GOODS_OFFER,
                    mLogic.mBiggerShipCapacity, mLogic.mBiggerShipPrice, goodsString, inventory);
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
        findViewById(R.id.simple_new_day_event_layout).setBackgroundResource(R.drawable.crew_accept_offer);
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
                            String state = getString(mLogic.mWeatherState.toStringId());
                            message = mActivity.getString(R.string.DANGER_FOG_MESSAGE, state);
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
        if (mLogic.mTutorial.isSuggestToFixShip()) {
            showAlertDialogMessage(getString(R.string.TUTORIAL_FIX_SHIP), getString(R.string.TUTORIAL_TITLE));
            blinkFixShip();
            return;
        }

        if (mLogic.mTutorial.isSuggestToSell()) {
            Goods goods = mLogic.mTutorial.mGoodsToDeal;
            State state = mLogic.mCurrentState;
            String goodsString = getString(goods.toStringId());
            String stateString = getString(state.toStringId());
            int price = mLogic.mPriceTable.getPrice(state, goods);
            int units = mLogic.getInventory(goods);

            String message = mActivity.getString(R.string.TUTORIAL_SELL, stateString, goodsString, price, units);
            showAlertDialogMessage(message, getString(R.string.TUTORIAL_TITLE));
            blinkMarket(goods);
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
            blinkMarket(goods);
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
            blinkSail(mLogic.mTutorial.mStateToSail);
            return;
        }

        blinkSleep();
        showAlertDialogMessage(getString(R.string.TUTORIAL_SLEEP), getString(R.string.TUTORIAL_TITLE));
    }

    private void blinkInventory(boolean isRed) {
        @DrawableRes int marketBackGroundId = isRed ? R.drawable.market_help_red : R.drawable.market_help;
        findViewById(R.id.market_help).setBackgroundResource(marketBackGroundId);

        if (mGoodsToBlink == null && isRed) {
            return;
        }

        LinearLayout goodsLayout = findViewById(R.id.goods_buttons);

        if (mGoodsToBlink == null || !isRed) {
            for (Goods goods: Goods.values()) {
                Button goodsButton = (Button)goodsLayout.getChildAt(goods.getValue());
                @DrawableRes int backGroundId = isRed ? goods.toWideButtonRedId() : goods.toWideButtonId();
                goodsButton.setBackgroundResource(backGroundId);
            }
        } else {
            Button goodsButton = (Button)goodsLayout.getChildAt(mGoodsToBlink.getValue());
            @DrawableRes int backGroundId = isRed ? mGoodsToBlink.toWideButtonRedId() : mGoodsToBlink.toWideButtonId();
            goodsButton.setBackgroundResource(backGroundId);
        }
    }

    private void blinkFlags(boolean isRed) {
        @DrawableRes int backGroundId = isRed ? R.drawable.sail_help_red : R.drawable.sail_help;
        findViewById(R.id.sail_help).setBackgroundResource(backGroundId);

        LinearLayout statesLayout = findViewById(R.id.prices_layout);

        if (mStateToBlink == null && isRed) {
            return;
        }

        if (mStateToBlink == null || !isRed) {
            for (State state : State.values()) {
                LinearLayout stateLayout = (LinearLayout)statesLayout.getChildAt(state.getValue());
                RelativeLayout flagButtonWrapper = (RelativeLayout)stateLayout.getChildAt(0);
                flagButtonWrapper.setAlpha(isRed ? 0.3f : 1.0f);
            }
        } else {
            LinearLayout stateLayout = (LinearLayout)statesLayout.getChildAt(mStateToBlink.getValue());
            RelativeLayout flagButtonWrapper = (RelativeLayout)stateLayout.getChildAt(0);
            flagButtonWrapper.setAlpha(isRed ? 0.3f : 1.0f);
        }
    }

    private void blinkSleepButton(boolean isRed) {
        @DrawableRes int backGroundId;
        if (mLogic.mCurrentDay.isLastDay()) {
            backGroundId = isRed ? R.drawable.wide_end_game_button_red : R.drawable.wide_end_game_button;
        } else {
            backGroundId = isRed ? R.drawable.wide_sleep_button_red : R.drawable.wide_sleep_button;
        }

        findViewById(R.id.wide_sleep_button).setBackgroundResource(backGroundId);
    }

    private void blinkFixShipButton(boolean isRed) {
        @DrawableRes int backGroundId;
        if (mLogic.isShipBroken()) {
            backGroundId = isRed ? R.drawable.wide_fix_button_red : R.drawable.wide_fix_button;
        } else {
            backGroundId = R.drawable.wide_ship_button;
        }
        findViewById(R.id.wide_fix_button).setBackgroundResource(backGroundId);
    }

    private void blinkBankButton(boolean isRed) {
        @DrawableRes int backGroundId;
        backGroundId = isRed ? R.drawable.wide_bank_button_red : R.drawable.wide_bank_button;
        ((Button)findViewById(R.id.wide_bank_button)).setBackgroundResource(backGroundId);

        backGroundId = isRed ? R.drawable.wide_cash_button_red : R.drawable.wide_cash_button;
        ((Button)findViewById(R.id.wide_cash_button)).setBackgroundResource(backGroundId);
    }

    public void timerBlinked(FrontEndTimer timer, int countDown) {
        if (timer == mFrontEndMarketBlinkTimer) {
            blinkInventory(countDown % 2 != 0);
        } else if (timer == mFrontEndSailBlinkTimer) {
            blinkFlags(countDown % 2 != 0);
        } else if (timer == mFrontEndSleepBlinkTimer) {
            blinkSleepButton(countDown % 2 != 0);
        } else if (timer == mFrontEndFixShipBlinkTimer) {
            blinkFixShipButton(countDown % 2 != 0);
        } else if (timer == mFrontEndBankBlinkTimer) {
            blinkBankButton(countDown % 2 != 0);
        }

    }

    public void stopBlinks() {
        mFrontEndMarketBlinkTimer.stop();
        mFrontEndSailBlinkTimer.stop();
        mFrontEndSleepBlinkTimer.stop();
        mFrontEndFixShipBlinkTimer.stop();
        mFrontEndBankBlinkTimer.stop();

        blinkInventory(false);
        blinkFlags(false);
        blinkSleepButton(false);
        blinkFixShipButton(false);
        blinkBankButton(false);
    }

    public void blinkBank() {
        mFrontEndBankBlinkTimer.startTimer(500, 20);
    }

    public void blinkMarket(Goods goods) {
        mGoodsToBlink = goods;
        mFrontEndMarketBlinkTimer.startTimer(500, 20);
    }

    public void blinkSail(State state) {
        mStateToBlink = state;
        mFrontEndSailBlinkTimer.startTimer(500, 20);
    }

    public void blinkSleep() {
        mFrontEndSleepBlinkTimer.startTimer(500, 20);
    }

    public void blinkFixShip() { mFrontEndFixShipBlinkTimer.startTimer(500,20);}

    public void blinkButtons() {
        if (mLogic.calculateLoad() > 0) {
            if (mLogic.getDayPart() != DayPart.NIGHT) {
                blinkSail(null);
            }
        } else {
            blinkMarket(null);
        }

        if (mLogic.getDayPart() == DayPart.NIGHT) {
            blinkSleep();
        }

        if (mLogic.canFixShip()) {
            blinkFixShip();
        }
    }

    public void showSleepQuestion() {
        ((TextView)findViewById(R.id.sleep_message)).setText(mLogic.mCurrentDay.isLastDay() ?
                R.string.END_GAME_QUESTION:R.string.SLEEP_QUESTION);
    }

    void showNewGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(getString(R.string.START_GAME_TITLE));
        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActivity.startNewGame();
            }
        });
        builder.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setIcon(R.drawable.boat_right);
        builder.show();
    }

    void showStartingTutorialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(R.string.START_TUTORIAL_QUESTION);
        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActivity.startTutorial();
            }
        });
        builder.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setIcon(R.drawable.boat_right);
        builder.show();
    }

    void showEndTutorialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(R.string.END_TUTORIAL_QUESTION);
        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActivity.startNewGame();
            }
        });
        builder.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setIcon(R.drawable.boat_right);
        builder.show();
    }

    void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(getString(R.string.EXIT_GAME_TITLE));
        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActivity.exit();
            }
        });
        builder.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setIcon(R.drawable.exit_icon);
        builder.show();
    }

    public void setLocale(String languageCode) {
        String currentLanguage = mActivity.getResources().getConfiguration().getLocales().get(0).getLanguage();
        boolean isCurrentHebrew = (currentLanguage.equals("iw") || currentLanguage.equals("he"));
        if (languageCode.equals("he") == isCurrentHebrew) {
            return;
        }

        mActivity.mCurrentLanguage = languageCode;
        mActivity.storeState(); // looks like exit() won't call it.

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = mActivity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        Intent mStartActivity = new Intent(mActivity, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(mActivity, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)mActivity.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        mActivity.exit();
    }

    public void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(getString(R.string.LANGUAGE));
        builder.setPositiveButton(getString(R.string.ENGLISH), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setLocale("en");
            }
        });
        builder.setNegativeButton(getString(R.string.HEBREW), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setLocale("he");
            }
        });
        builder.setIcon(R.drawable.help_icon);
        builder.show();
    }

    void showSignGoogleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(getString(R.string.SIGN_IN_QUESTION));
        builder.setMessage(getString(R.string.SIGN_IN_BENIFITS));
        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActivity.mBackEndGoogleApi.signIn();
            }
        });
        builder.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActivity.mIsGoogleSignIn = false;
                ((CheckBox)findViewById(R.id.connect_google_play_games)).setChecked(false);
            }
        });
        builder.setIcon(R.drawable.boat_right);
        builder.show();
    }

    public void priceClick(View priceView) {
        ViewGroup priceViewParent = (ViewGroup) priceView.getParent();
        int indexOfChild = priceViewParent.indexOfChild(priceView);
        Goods goods = Goods.values()[indexOfChild - 1]; // zero for flag

        ViewGroup priceViewGrandParent = (ViewGroup) priceViewParent.getParent();
        int indexOfParent = priceViewGrandParent.indexOfChild(priceViewParent);
        State destinationState = State.values()[indexOfParent];

        int destinationPrice = mLogic.mPriceTable.getPrice(destinationState, goods);
        int sourcePrice = mLogic.mPriceTable.getPrice(mLogic.mCurrentState, goods);
        int maxUnits = Math.min(mLogic.calculateTotalValue() / sourcePrice, mLogic.mCapacity);
        int profit = (destinationPrice - sourcePrice) * maxUnits;

        String calculatorString =
                "(" + destinationPrice + "-" + sourcePrice + ")X" + maxUnits + "=" + profit;
        ((TextView)findViewById(R.id.calculator)).setText(calculatorString);
    }

    // For Start Tutorial
    public void setStateVisibility(State state, boolean isVisible) {
        ((LinearLayout)findViewById(R.id.prices_layout)).getChildAt(state.getValue()).
                setVisibility(isVisible? View.VISIBLE:View.INVISIBLE);
    }

    public void setGoodsVisibility(Goods goods, boolean isVisible) {
        int visible = isVisible ? View.VISIBLE : View.INVISIBLE;

        for (State state : State.values()) {
            LinearLayout stateLayout =
                    (LinearLayout)((LinearLayout) findViewById(R.id.prices_layout)).getChildAt(state.getValue());
            stateLayout.getChildAt(goods.getValue() + 1).
                    setVisibility(visible);
        }

        setViewEnable(((LinearLayout)findViewById(R.id.goods_buttons)).getChildAt(goods.getValue()), isVisible);
    }

    public void setCalculatorVisibility(boolean isVisible) {
        setViewEnable(R.id.calculator, isVisible);
    }

    public void setTutorialVisibility(boolean isVisible) {
        setViewEnable(R.id.tutorial_button, isVisible);
    }

    public void setBankVisibility(boolean isVisible) {
        setViewEnable(R.id.wide_cash_button, isVisible);
        setViewEnable(R.id.wide_bank_button, isVisible);
    }

    public void setSleepVisibility(boolean isVisible) {
        setViewEnable(R.id.wide_sleep_button, isVisible);
    }

    public void setFixVisibility(boolean isVisible) {
        setViewEnable(R.id.wide_fix_button, isVisible);
    }

    public void setCapacityVisibility(boolean isVisible) {
        setViewEnable(R.id.wide_capacity_button, isVisible);
    }

    public void showTutorialStrings(final String string1, final String string2) {
        ((TextView)findViewById((R.id.current_weather_text_view))).setText(string1);
        ((TextView)findViewById((R.id.market_state_text_view))).setText(string2);

        final String string = string1 + " " +string2;
        final String title =  getString(R.string.TUTORIAL);
        showDelayedMessage(string, title);
    }

    private TimerTask mPreviousMessageTask = null;

    public boolean isDelayedMessageExist() {
        return mPreviousMessageTask != null;
    }

    public void cancelDelayedMessage() {
        if (mPreviousMessageTask != null) {
            mPreviousMessageTask.cancel();
            mPreviousMessageTask = null;
        }
    }

    public void showLastDelayedMessage() {
        if (mPreviousMessageTask != null) {
            mPreviousMessageTask.run();
        }
    }

    public void showDelayedMessage(final String string, final String title) {
        final Runnable timerTick = new Runnable() {
            public void run() {
                if (getCurrentVisibleWindow() != Window.MENU_WINDOW) {
                    mPreviousMessageTask.cancel();
                    showAlertDialogMessage(string, title);
                    mPreviousMessageTask = null;
                }
            }
        };

        Timer timer = new Timer();
        TimerTask timerTask = (new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(timerTick);
            }
        });

        if (mPreviousMessageTask != null) {
            mPreviousMessageTask.cancel();
        }

        mPreviousMessageTask = timerTask;

        int milliseconds = 2000;
        timer.schedule(timerTask, milliseconds);
    }

    public void resetMarketStateText() {
        ((TextView)findViewById(R.id.market_state_text_view)).setText(R.string.MARKET_STATE);
    }

    public void setStartingTutorialButtonText() {
        String text = mActivity.mIsStartTutorialActive ? getString(R.string.END_TUTORIAL) : getString(R.string.START_TUTORIAL);
        ((Button)findViewById(R.id.start_tutorial_button)).setText(text);
    }
}
