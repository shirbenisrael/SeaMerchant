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
        LinearLayout inventoryLayout = findViewById(R.id.inventory_for_pirates);
        String goodString = getString(goods.toStringId());
        SeekBar seekBar = (SeekBar)inventoryLayout.getChildAt(goods.getValue() * 2 + 1);

        int maxUnits = mLogic.mInventory[goods.getValue()];
        String goodToGive = mActivity.getString(R.string.GOODS_TO_OFFSET, goodString, seekBar.getProgress(), maxUnits);

        TextView textView = (TextView)inventoryLayout.getChildAt(goods.getValue() * 2);
        textView.setText(goodToGive);
    }

    private void updateCashOffer() {
        TextView textView = findViewById(R.id.negotiate_cash_text);
        SeekBar seekBar = findViewById(R.id.negotiate_cash_seek_bar);
        int maxUnits = mLogic.mCash;
        String cashToGive = mActivity.getString(R.string.CASH_TO_OFFER, seekBar.getProgress(), maxUnits);
        textView.setText(cashToGive);
    }

    public void showNegotiatePirates() {
        LinearLayout inventoryLayout = findViewById(R.id.inventory_for_pirates);
        for (Goods goods : Goods.values()) {
            SeekBar seekBar = (SeekBar)inventoryLayout.getChildAt(goods.getValue() * 2 + 1);
            int maxUnits = mLogic.mInventory[goods.getValue()];
            seekBar.setMax(maxUnits);
            seekBar.setProgress(maxUnits / 2);

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
        int maxUnits = mLogic.mCash;
        seekBar.setMax(maxUnits);
        seekBar.setProgress(maxUnits / 2);

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

    public boolean sendOfferToPirates() {
        LinearLayout inventoryLayout = findViewById(R.id.inventory_for_pirates);
        int[] goodToOffer = new int[Goods.NUM_GOODS_TYPES];
        for (Goods goods : Goods.values()) {
            SeekBar seekBar = (SeekBar)inventoryLayout.getChildAt(goods.getValue() * 2 + 1);
            goodToOffer[goods.getValue()] = seekBar.getProgress();
        }

        SeekBar seekBar = findViewById(R.id.negotiate_cash_seek_bar);
        int cashToOffer = seekBar.getProgress();

        return mLogic.sendOfferToPirates(goodToOffer, cashToOffer);
    }
}
