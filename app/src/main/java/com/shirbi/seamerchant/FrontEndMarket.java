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
                    mLogic.mMarketDeal.setGoods(progress);
                    showDealState();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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

        SeekBar seekBar = (SeekBar)findViewById(R.id.market_seek_bar);
        seekBar.setProgress(mLogic.mMarketDeal.mGoodsUnits);
    }

    public void onMarketClick() {
        Goods goods = mLogic.mMarketDeal.mGoods;

        String holdQuestion = mActivity.getString(R.string.HOLD_QUESTION, getString(goods.toStringId()));
        ((TextView)findViewById(R.id.market_hold_question)).setText(holdQuestion);

        ((ImageView)findViewById(R.id.market_goods_image)).setImageResource(goods.toMainImageId());

        SeekBar seekBar = (SeekBar)findViewById(R.id.market_seek_bar);
        seekBar.setMax(mLogic.mMarketDeal.mMaxUnitsToHold);

        showDealState();

        findViewById(R.id.main_window_layout).setVisibility(View.GONE);
        findViewById(R.id.market_layout).setVisibility(View.VISIBLE);

        String operationTimeString = mLogic.isMarketOperationTakesTime() ?
                getString(R.string.OPERATION_ONE_HOUR) : getString(R.string.OPERATION_NO_TIME);
        ((TextView)findViewById(R.id.market_operation_time)).setText(operationTimeString);
    }
}
