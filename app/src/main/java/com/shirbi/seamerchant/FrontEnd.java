package com.shirbi.seamerchant;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

public class FrontEnd {
    MainActivity mActivity;

    private <T extends View> T findViewById(@IdRes int id) {
        return mActivity.getWindow().findViewById(id);
    }

    public final String getString(@StringRes int resId) {
        return mActivity.getString(resId);
    }

    public FrontEnd(MainActivity activity) {
        mActivity = activity;
    }

    public void ShowPrices() {
        LinearLayout statesLayout = findViewById(R.id.prices_layout);
        for (State state : State.values()) {
            LinearLayout stateLayout = (LinearLayout)statesLayout.getChildAt(state.getValue());

            for (Goods goods: Goods.values()) {
                TextView textView = (TextView)stateLayout.getChildAt(goods.getValue() + 1);
                textView.setText(String.valueOf(state.getValue() + goods.getValue()));
            }
        }
    }
}
