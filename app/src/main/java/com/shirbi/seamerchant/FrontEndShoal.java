package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndShoal extends FrontEndGeneric {

    public FrontEndShoal(MainActivity activity) {
        super(activity);
    }

    public void showShoal() {
        Sail sail = mLogic.mSail;
        String message = mActivity.getString(R.string.SHOAL_DAMAGE, sail.mShoalDamage);
        ((TextView)findViewById(R.id.shoal_message)).setText(message);
    }
}
