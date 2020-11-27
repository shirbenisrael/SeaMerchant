package com.shirbi.seamerchant;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FrontEnd extends FrontEndGeneric {
    public FrontEnd(MainActivity activity) {
        super(activity);

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

        textView = findViewById(R.id.current_hour_text_view);
        textView.setText(String.valueOf(mLogic.mCurrentHour) + ":00");

        textView = findViewById(R.id.current_day_text_view);
        textView.setText(getString(mLogic.mCurrentDay.toStringId()));

        textView = findViewById(R.id.current_state_text_view);
        textView.setText(getString(mLogic.mCurrentState.toStringId()));

        textView = findViewById(R.id.wide_capacity_button);
        textView.setText(String.valueOf(mLogic.mCapacity) + " " + getString(R.string.TONNE));

        textView = findViewById(R.id.wide_cash_button);
        textView.setText(mActivity.getString(R.string.MONEY_STRING, mLogic.mCash));

        textView = findViewById(R.id.wide_bank_button);
        textView.setText(mActivity.getString(R.string.MONEY_STRING, mLogic.mBankDeposit));

        findViewById(R.id.main_window_layout).setBackgroundResource(mLogic.getDayPart().toImageId());
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
            if (stateLayout.getChildAt(0) == view) {
                return State.values()[i];
            }
        }
        return null;
    }

    public void exitMarket() {
        findViewById(R.id.market_layout).setVisibility(View.GONE);
        findViewById(R.id.main_window_layout).setVisibility(View.VISIBLE);
    }

    public void showSailWindow() {
        findViewById(R.id.sail_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.main_window_layout).setVisibility(View.GONE);
    }

    public void exitSail() {
        findViewById(R.id.sail_layout).setVisibility(View.GONE);
        findViewById(R.id.main_window_layout).setVisibility(View.VISIBLE);
    }

    public void endSail() {
        findViewById(R.id.sail_layout).setVisibility(View.GONE);
        findViewById(R.id.end_of_sail_layout).setVisibility(View.VISIBLE);
    }

    public void closeEndSailWindow() {
        findViewById(R.id.end_of_sail_layout).setVisibility(View.GONE);
        findViewById(R.id.main_window_layout).setVisibility(View.VISIBLE);
    }
}
