package com.shirbi.seamerchant;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

public class FrontEnd {
    MainActivity mActivity;
    Logic mLogic;

    private <T extends View> T findViewById(@IdRes int id) {
        return mActivity.getWindow().findViewById(id);
    }

    public final String getString(@StringRes int resId) {
        return mActivity.getString(resId);
    }

    public FrontEnd(MainActivity activity) {
        mActivity = activity;
        mLogic = activity.mLogic;

        LinearLayout goodsLayout = findViewById(R.id.goods_buttons);
        for (Goods goods: Goods.values()) {
            Button imageButton = (Button)goodsLayout.getChildAt(goods.getValue());
            imageButton.setBackgroundResource(goods.toWideButtonId());
        }

        LinearLayout statesLayout = findViewById(R.id.prices_layout);
        for (State state : State.values()) {
            LinearLayout stateLayout = (LinearLayout)statesLayout.getChildAt(state.getValue());

            Button flagButton = (Button)stateLayout.getChildAt(0);
            flagButton.setBackgroundResource(state.toFlagId());
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

        TextView textView;

        textView = findViewById(R.id.current_hour_text_view);
        textView.setText(String.valueOf(mLogic.mCurrentHour) + ":00");

        textView = findViewById(R.id.current_day_text_view);
        textView.setText(getString(mLogic.mCurrentDay.toStringId()));

        textView = findViewById(R.id.current_state_text_view);
        textView.setText(getString(mLogic.mCurrentState.toStringId()));

        textView = findViewById(R.id.wide_capacity_button);
        textView.setText(String.valueOf(mLogic.mCapacity) + " " + getString(R.string.TONNE));
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

    public void showDealState() {
        Goods goods = mLogic.mMarketDeal.mGoods;

        String units = getString(goods.toStringId()) + ": " + mLogic.mMarketDeal.mGoodsUnits + " " +
                getString(R.string.TONNE);
        ((TextView)findViewById(R.id.market_goods_units)).setText(units);

        String cash = getString(R.string.CASH) + ": " + mLogic.mMarketDeal.mCash + " " +
                getString(R.string.CASH_UNITS);
        ((TextView)findViewById(R.id.market_cash)).setText(cash);

        String value = getString(R.string.TOTAL_VALUE) + ": " + mLogic.mMarketDeal.getGoodsValue() + " " +
                getString(R.string.CASH_UNITS);
        ((TextView)findViewById(R.id.goods_value)).setText(value);
    }

    public void onMarketClick() {
        Goods goods = mLogic.mMarketDeal.mGoods;
        ((ImageView)findViewById(R.id.market_goods_image)).setImageResource(goods.toMainImageId());

        showDealState();

        findViewById(R.id.main_window_layout).setVisibility(View.GONE);
        findViewById(R.id.market_layout).setVisibility(View.VISIBLE);
    }

    public void exitMarket() {
        findViewById(R.id.market_layout).setVisibility(View.GONE);
        findViewById(R.id.main_window_layout).setVisibility(View.VISIBLE);
    }
}
