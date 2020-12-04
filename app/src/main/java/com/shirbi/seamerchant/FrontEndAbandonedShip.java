package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndAbandonedShip extends FrontEndGeneric {

    public FrontEndAbandonedShip(MainActivity activity) {
        super(activity);
    }

    public void showAbandonedShip() {
        Sail sail = mLogic.mSail;
        String goods = getString(sail.mAbandonedShipGoods.toStringId());
        String message = mActivity.getString(R.string.ABANDONED_SHIP_INVENTORY, sail.mAbandonedShipGoodsUnits, goods);
        ((TextView)findViewById(R.id.abandoned_ship_message)).setText(message);
    }
}
