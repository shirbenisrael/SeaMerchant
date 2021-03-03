package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndAbandonedShip extends FrontEndGeneric {

    public FrontEndAbandonedShip(MainActivity activity) {
        super(activity);
    }

    public void showAbandonedShip() {
        Sail sail = mLogic.mSail;
        String goods = getGoodsString(sail.mAbandonedShipGoods);
        String message = mActivity.getString(R.string.ABANDONED_SHIP_INVENTORY, mDecimalFormat.format(sail.mAbandonedShipGoodsUnits), goods);
        ((TextView)findViewById(R.id.abandoned_ship_message)).setText(message);
    }
}
