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
                    mLogic.mFixShipDeal.setFix(mLogic.mFixShipDeal.mMaxFix * progress / MAX_SEEK_BAR_UNITS);
                    showDealState();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void showDealState() {
        String lock = mActivity.getString(R.string.FIXING_STRING, mDecimalFormat.format(mLogic.mFixShipDeal.mCurrentFix));
        ((TextView)findViewById(R.id.fix_ship_units)).setText(lock);

        String cash = mActivity.getString(R.string.CASH_STRING, mDecimalFormat.format(mLogic.mFixShipDeal.mCash));
        ((TextView)findViewById(R.id.fix_cash_units)).setText(cash);

        SeekBar seekBar = (SeekBar)findViewById(R.id.fix_seek_bar);
        seekBar.setProgress((int)(MAX_SEEK_BAR_UNITS * mLogic.mFixShipDeal.mCurrentFix / mLogic.mFixShipDeal.mMaxFix));
    }

    public void onFixShipClick() {
        SeekBar seekBar = (SeekBar)findViewById(R.id.fix_seek_bar);
        seekBar.setMax(MAX_SEEK_BAR_UNITS);
        showDealState();

        String operationTimeString = mLogic.isFixOperationTakesTime() ?
                getString(R.string.OPERATION_ONE_HOUR) : getString(R.string.OPERATION_NO_TIME);
        ((TextView)findViewById(R.id.fix_operation_time)).setText(operationTimeString);
    }

    public void fixAsPossible() {
        mLogic.mFixShipDeal.fixAsPossible();
        showDealState();
    }
}
