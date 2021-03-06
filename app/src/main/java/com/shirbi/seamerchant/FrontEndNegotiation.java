package com.shirbi.seamerchant;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class FrontEndNegotiation extends FrontEndGeneric {
    public FrontEndNegotiation(MainActivity activity) {
        super(activity);
    }

    private void updateGoodsOffer(Goods goods) {
        LinearLayout inventoryLayout = findViewById(R.id.inventory_to_offer);
        String goodString = getString(goods.toStringId());
        SeekBar seekBar = (SeekBar)inventoryLayout.getChildAt(goods.getValue() * 2 + 1);

        long maxUnits = mLogic.mInventory[goods.getValue()];
        long goodToGiveLong = seekBar.getProgress() * maxUnits / MAX_SEEK_BAR_UNITS;
        String goodToGive = mActivity.getString(R.string.GOODS_TO_OFFSET, goodString, goodToGiveLong, maxUnits);

        TextView textView = (TextView)inventoryLayout.getChildAt(goods.getValue() * 2);
        textView.setText(goodToGive);
    }

    private void updateCashOffer() {
        TextView textView = findViewById(R.id.negotiate_cash_text);
        SeekBar seekBar = findViewById(R.id.negotiate_cash_seek_bar);
        long maxUnits = mLogic.mCash;
        long cashToGiveLong = seekBar.getProgress() * maxUnits / MAX_SEEK_BAR_UNITS;
        String cashToGive = mActivity.getString(R.string.CASH_TO_OFFER, cashToGiveLong, maxUnits);
        textView.setText(cashToGive);
    }

    public void showNegotiation() {
        findViewById(R.id.negotiate_layout).setBackgroundResource(mLogic.mNegotiationType == Logic.NegotiationType.PIRATES ?
                R.drawable.pirate : R.drawable.strike);

        ((TextView)findViewById(R.id.offer_question)).setText(mLogic.mNegotiationType == Logic.NegotiationType.PIRATES ?
                R.string.OFFER_TO_PIRATES : R.string.OFFER_TO_CREW);

        LinearLayout inventoryLayout = findViewById(R.id.inventory_to_offer);
        for (Goods goods : Goods.values()) {
            SeekBar seekBar = (SeekBar)inventoryLayout.getChildAt(goods.getValue() * 2 + 1);
            seekBar.setMax(MAX_SEEK_BAR_UNITS);
            seekBar.setProgress(MAX_SEEK_BAR_UNITS / 2);

            updateGoodsOffer(goods);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int indexView = ((ViewGroup) seekBar.getParent()).indexOfChild(seekBar);
                        updateGoodsOffer(Goods.values()[indexView/2]);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {}
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        SeekBar seekBar = findViewById(R.id.negotiate_cash_seek_bar);
        seekBar.setMax(MAX_SEEK_BAR_UNITS);
        seekBar.setProgress(MAX_SEEK_BAR_UNITS / 2);

        updateCashOffer();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int indexView = ((ViewGroup) seekBar.getParent()).indexOfChild(seekBar);
                    updateCashOffer();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public boolean sendOffer() {
        LinearLayout inventoryLayout = findViewById(R.id.inventory_to_offer);
        long[] goodToOffer = new long[Goods.NUM_GOODS_TYPES];
        for (Goods goods : Goods.values()) {
            SeekBar seekBar = (SeekBar)inventoryLayout.getChildAt(goods.getValue() * 2 + 1);
            long maxUnits = mLogic.mInventory[goods.getValue()];
            goodToOffer[goods.getValue()] = seekBar.getProgress() * maxUnits / MAX_SEEK_BAR_UNITS;
        }

        SeekBar seekBar = findViewById(R.id.negotiate_cash_seek_bar);
        long cashToOffer = seekBar.getProgress() * mLogic.mCash / MAX_SEEK_BAR_UNITS;

        return mLogic.sendOffer(goodToOffer, cashToOffer);
    }
}
