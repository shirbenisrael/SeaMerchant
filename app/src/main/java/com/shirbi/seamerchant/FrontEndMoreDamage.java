package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndMoreDamage extends FrontEndGeneric {

    public FrontEndMoreDamage(MainActivity activity) {
        super(activity);
    }

    public void showDamage() {
        Sail sail = mLogic.mSail;
        String message = mActivity.getString(R.string.MORE_DAMAGE_RESULT, mDecimalFormat.format(sail.mMoreDamage));
        ((TextView)findViewById(R.id.more_damage_message)).setText(message);
    }
}
