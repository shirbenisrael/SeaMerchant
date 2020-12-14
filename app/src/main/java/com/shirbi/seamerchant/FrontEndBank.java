package com.shirbi.seamerchant;

import android.widget.SeekBar;
import android.widget.TextView;

public class FrontEndBank extends FrontEndGeneric {

    public FrontEndBank(MainActivity activity) {
        super(activity);

        SeekBar bankSeekBar = (SeekBar) findViewById(R.id.bank_seek_bar);
        bankSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mLogic.mBankDeal.setDeposit(progress);
                    showDealState();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void onBankClick() {
        SeekBar seekBar = (SeekBar)findViewById(R.id.bank_seek_bar);
        seekBar.setMax(mLogic.mBankDeal.mMaxDeposit);
        showDealState();
    }

    public void showDealState() {
        String lock = mActivity.getString(R.string.LOCK_STRING, mLogic.mBankDeal.mDeposit);
        ((TextView)findViewById(R.id.bank_lock_units)).setText(lock);

        String cash = mActivity.getString(R.string.CASH_STRING, mLogic.mBankDeal.mCash);
        ((TextView)findViewById(R.id.bank_cash_units)).setText(cash);

        SeekBar seekBar = (SeekBar)findViewById(R.id.bank_seek_bar);
        seekBar.setProgress(mLogic.mBankDeal.mDeposit);
    }
}
