package com.shirbi.seamerchant;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class FrontEndBank extends FrontEndGeneric {

    public FrontEndBank(MainActivity activity) {
        super(activity);

        SeekBar bankSeekBar = (SeekBar) findViewById(R.id.bank_seek_bar);
        bankSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mLogic.mBankDeal.setDeposit(mLogic.mBankDeal.mMaxDeposit * progress / MAX_SEEK_BAR_UNITS);
                    showDealState();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void onBankClick() {
        SeekBar seekBar = (SeekBar)findViewById(R.id.bank_seek_bar);
        seekBar.setMax(MAX_SEEK_BAR_UNITS);
        showDealState();

        String tipString = mActivity.getString(R.string.BANK_TIP, mLogic.getBankNightlyInterest());
        ((TextView)findViewById(R.id.bank_interest)).setText(tipString);

        String operationTimeString = mLogic.isBankOperationTakesTime() ?
                getString(R.string.OPERATION_ONE_HOUR) : getString(R.string.OPERATION_NO_TIME);
        ((TextView)findViewById(R.id.bank_operation_time)).setText(operationTimeString);

        String question = mActivity.getString(R.string.BANK_QUESTION, mDecimalFormat.format(mLogic.calculateInventoryValue()));
        ((TextView)findViewById(R.id.bank_hold_question)).setText(question);

        ((Button)findViewById(R.id.bank_cash_for_guards_button)).setText(
                mActivity.getString(R.string.BANK_CASH_FOR_GUARDS, mLogic.getDefaultNumGuards()));
    }

    public void showDealState() {
        String lock = mActivity.getString(R.string.LOCK_STRING, mDecimalFormat.format(mLogic.mBankDeal.mDeposit));
        ((TextView)findViewById(R.id.bank_lock_units)).setText(lock);

        String cash = mActivity.getString(R.string.CASH_STRING, mDecimalFormat.format(mLogic.mBankDeal.mCash));
        ((TextView)findViewById(R.id.bank_cash_units)).setText(cash);

        SeekBar seekBar = (SeekBar)findViewById(R.id.bank_seek_bar);
        if (mLogic.mBankDeal.mMaxDeposit > 0) {
            seekBar.setProgress((int) (MAX_SEEK_BAR_UNITS * mLogic.mBankDeal.mDeposit / mLogic.mBankDeal.mMaxDeposit));
        } else {
            seekBar.setProgress(0);
        }
    }

    public void showOnlyDepositAll() {
        enableButton(R.id.bank_deposit_all_button);
        disableButton(R.id.bank_cash_for_guards_button);
        disableButton(R.id.bank_draw_all_button);
        disableButton(R.id.bank_plus_10000_button);
        disableButton(R.id.bank_plus_1000_button);
        disableButton(R.id.bank_minus_1000_button);
        disableButton(R.id.bank_minus_10000_button);
        disableButton(R.id.bank_seek_bar);
        disableButton(R.id.bank_cancel_button);
    }

    public void showOnlyDrawAll() {
        disableButton(R.id.bank_deposit_all_button);
        disableButton(R.id.bank_cash_for_guards_button);
        enableButton(R.id.bank_draw_all_button);
        disableButton(R.id.bank_plus_10000_button);
        disableButton(R.id.bank_plus_1000_button);
        disableButton(R.id.bank_minus_1000_button);
        disableButton(R.id.bank_minus_10000_button);
        disableButton(R.id.bank_seek_bar);
        disableButton(R.id.bank_cancel_button);
    }

    public void showAllButtons() {
        enableButton(R.id.bank_deposit_all_button);
        enableButton(R.id.bank_cash_for_guards_button);
        enableButton(R.id.bank_draw_all_button);
        enableButton(R.id.bank_plus_10000_button);
        enableButton(R.id.bank_plus_1000_button);
        enableButton(R.id.bank_minus_1000_button);
        enableButton(R.id.bank_minus_10000_button);
        enableButton(R.id.bank_seek_bar);
        enableButton(R.id.bank_cancel_button);
    }
}
