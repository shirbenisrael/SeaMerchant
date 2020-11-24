package com.shirbi.seamerchant;

import android.view.View;
import android.widget.Button;
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
}
