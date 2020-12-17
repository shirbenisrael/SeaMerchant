package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndSink extends FrontEndGeneric {

    public FrontEndSink(MainActivity activity) {
        super(activity);
    }

    public void showSink() {
        Sail sail = mLogic.mSail;
        String goods = getString(sail.mSinkGood.toStringId());
        String message = mActivity.getString(R.string.SINK_DAMAGE, sail.mSinkGoodsUnitsLost, goods);
        ((TextView)findViewById(R.id.sink_message)).setText(message);
    }
}
