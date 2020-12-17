package com.shirbi.seamerchant;

import android.widget.SeekBar;
import android.widget.TextView;

public class FrontEndFixShip  extends FrontEndGeneric {

    public FrontEndFixShip(MainActivity activity) {
        super(activity);

        SeekBar bankSeekBar = (SeekBar) findViewById(R.id.fix_seek_bar);
        bankSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mLogic.mFixShipDeal.setFix(progress);
                    showDealState();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void showDealState() {
        String lock = mActivity.getString(R.string.FIXING_STRING, mLogic.mFixShipDeal.mCurrentFix);
        ((TextView)findViewById(R.id.fix_ship_units)).setText(lock);

        String cash = mActivity.getString(R.string.CASH_STRING, mLogic.mFixShipDeal.mCash);
        ((TextView)findViewById(R.id.fix_cash_units)).setText(cash);

        SeekBar seekBar = (SeekBar)findViewById(R.id.fix_seek_bar);
        seekBar.setProgress(mLogic.mFixShipDeal.mCurrentFix);
    }

    public void onFixShipClick() {
        SeekBar seekBar = (SeekBar)findViewById(R.id.fix_seek_bar);
        seekBar.setMax(mLogic.mFixShipDeal.mMaxFix);
        showDealState();
    }

    public void fixAsPossible() {
        mLogic.mFixShipDeal.fixAsPossible();
        showDealState();
    }
}