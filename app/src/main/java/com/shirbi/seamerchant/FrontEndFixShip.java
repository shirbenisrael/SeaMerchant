package com.shirbi.seamerchant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
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
        if (mLogic.mFixShipDeal.mMaxFix > 0) {
            seekBar.setProgress((int) (MAX_SEEK_BAR_UNITS * mLogic.mFixShipDeal.mCurrentFix / mLogic.mFixShipDeal.mMaxFix));
        } else {
            seekBar.setProgress(0);
        }

        findViewById(R.id.one_free_fix_button).setVisibility(mLogic.isFreeFixButtonVisible() ? View.VISIBLE : View.INVISIBLE);
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

    public void showFreeFixDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(getString(R.string.ONE_FREE_FIX));
        builder.setMessage(getString(R.string.ONE_FREE_FIX_QUESTION));
        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActivity.freeFix();
            }
        });
        builder.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setIcon(R.drawable.fix);
        builder.show();
    }
}
