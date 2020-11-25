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

    private void ShowPrices(PriceTable priceTable) {
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

    public void ShowState() {
        ShowPrices(mLogic.mPriceTable);

        TextView textView;

        textView = findViewById(R.id.current_hour_text_view);
        textView.setText(String.valueOf(mLogic.mCurrentHour) + ":00");

        textView = findViewById(R.id.current_day_text_view);
        textView.setText(getString(mLogic.mCurrentDay.toStringId()));

        textView = findViewById(R.id.current_state_text_view);
        textView.setText(getString(mLogic.mCurrentState.toStringId()));
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

    public void onMarketClick(Goods goods) {
        ((ImageView)findViewById(R.id.market_goods_image)).setImageResource(goods.toMainImageId());

        findViewById(R.id.main_window_layout).setVisibility(View.GONE);
        findViewById(R.id.market_layout).setVisibility(View.VISIBLE);
    }

    public void onDealDoneClick(View view) {
        findViewById(R.id.market_layout).setVisibility(View.GONE);
        findViewById(R.id.main_window_layout).setVisibility(View.VISIBLE);
    }
}
