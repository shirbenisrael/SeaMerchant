package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndSink extends FrontEndGeneric {

    public FrontEndSink(MainActivity activity) {
        super(activity);
    }

    public void showSink() {
        Sail sail = mLogic.mSail;
        String goods = getGoodsString(sail.mSinkGood);
        String message = mActivity.getString(R.string.SINK_DAMAGE, mDecimalFormat.format(sail.mSinkGoodsUnitsLost), goods);
        ((TextView)findViewById(R.id.sink_message)).setText(message);
    }
}
