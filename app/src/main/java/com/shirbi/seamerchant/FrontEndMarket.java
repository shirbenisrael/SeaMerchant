package com.shirbi.seamerchant;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class FrontEndMarket extends FrontEndGeneric {
    public FrontEndMarket(MainActivity activity) {
        super(activity);

        SeekBar marketSeekBar = (SeekBar) findViewById(R.id.market_seek_bar);
        marketSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mLogic.mMarketDeal.setGoods(mLogic.mMarketDeal.mMaxUnitsToHold * progress / MAX_SEEK_BAR_UNITS);
                    showDealState();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void showDealState() {
        Goods goods = mLogic.mMarketDeal.mGoods;

        String units = getGoodsString(goods) + ": " + mDecimalFormat.format(mLogic.mMarketDeal.mGoodsUnits) + " " +
                getString(R.string.TONNE);
        ((TextView)findViewById(R.id.market_goods_units)).setText(units);

        String cash = getString(R.string.CASH) + ": " + mDecimalFormat.format(mLogic.mMarketDeal.mCash) + " " +
                getString(R.string.CASH_UNITS);
        ((TextView)findViewById(R.id.market_cash)).setText(cash);

        String value = getString(R.string.TOTAL_VALUE) + ": " + mDecimalFormat.format(mLogic.mMarketDeal.getGoodsValue()) + " " +
                getString(R.string.CASH_UNITS);
        ((TextView)findViewById(R.id.goods_value)).setText(value);

        SeekBar seekBar = (SeekBar)findViewById(R.id.market_seek_bar);
        seekBar.setProgress((int)(MAX_SEEK_BAR_UNITS * mLogic.mMarketDeal.mGoodsUnits / mLogic.mMarketDeal.mMaxUnitsToHold));
    }

    public void onMarketClick() {
        Goods goods = mLogic.mMarketDeal.mGoods;

        String holdQuestion = mActivity.getString(R.string.HOLD_QUESTION, getGoodsString(goods));
        ((TextView)findViewById(R.id.market_hold_question)).setText(holdQuestion);

        ((ImageView)findViewById(R.id.market_goods_image)).setImageResource(goods.toMainImageId());

        SeekBar seekBar = (SeekBar)findViewById(R.id.market_seek_bar);
        seekBar.setMax(MAX_SEEK_BAR_UNITS);

        showDealState();

        findViewById(R.id.main_window_layout).setVisibility(View.GONE);
        findViewById(R.id.market_layout).setVisibility(View.VISIBLE);

        String operationTimeString = mLogic.isMarketOperationTakesTime() ?
                getString(R.string.OPERATION_ONE_HOUR) : getString(R.string.OPERATION_NO_TIME);
        ((TextView)findViewById(R.id.market_operation_time)).setText(operationTimeString);
    }

    public void showAllButton() {
        enableButton(R.id.market_plus_10);
        enableButton(R.id.market_plus_1);
        enableButton(R.id.market_minus_1);
        enableButton(R.id.market_minus_10);
        enableButton(R.id.market_seek_bar);
        enableButton(R.id.market_percentage_with_guards);
        enableButton(R.id.market_fill_capacity_button);
        enableButton(R.id.market_sell_all_button);
        enableButton(R.id.market_buy_all_button);
    }

    public void showOnlyBuyAllButton() {
        disableButton(R.id.market_percentage_with_guards);
        disableButton(R.id.market_plus_10);
        disableButton(R.id.market_plus_1);
        disableButton(R.id.market_minus_1);
        disableButton(R.id.market_minus_10);
        disableButton(R.id.market_seek_bar);
        disableButton(R.id.market_fill_capacity_button);
        disableButton(R.id.market_sell_all_button);
        enableButton(R.id.market_buy_all_button);
    }

    public void showOnlySellAllButton() {
        disableButton(R.id.market_percentage_with_guards);
        disableButton(R.id.market_plus_10);
        disableButton(R.id.market_plus_1);
        disableButton(R.id.market_minus_1);
        disableButton(R.id.market_minus_10);
        disableButton(R.id.market_seek_bar);
        disableButton(R.id.market_fill_capacity_button);
        enableButton(R.id.market_sell_all_button);
        disableButton(R.id.market_buy_all_button);
    }

    public void showOnlyFillCapacity() {
        disableButton(R.id.market_percentage_with_guards);
        disableButton(R.id.market_plus_10);
        disableButton(R.id.market_plus_1);
        disableButton(R.id.market_minus_1);
        disableButton(R.id.market_minus_10);
        disableButton(R.id.market_seek_bar);
        enableButton(R.id.market_fill_capacity_button);
        disableButton(R.id.market_sell_all_button);
        disableButton(R.id.market_buy_all_button);
    }
}
