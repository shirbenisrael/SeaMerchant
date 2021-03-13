package com.shirbi.seamerchant;

import android.widget.TextView;

public class FrontEndFortuneTeller extends FrontEndGeneric {

    public FrontEndFortuneTeller(MainActivity activity) {
        super(activity);
    }

    public void showInitialMessage() {
        String stateString = getStateString(mLogic.mFortuneTellerInformation.state);
        String goodsString = getGoodsString(mLogic.mFortuneTellerInformation.goods);

        String questionString = mActivity.getString(R.string.FORTUNE_TELLER_QUESTION, goodsString, stateString);
        ((TextView)findViewById(R.id.fortune_teller_message)).setText(questionString);
    }

    public void showFortune() {
        String stateString = getStateString(mLogic.mFortuneTellerInformation.state);
        String goodsString = getGoodsString(mLogic.mFortuneTellerInformation.goods);
        int price = mLogic.mFortuneTellerInformation.price;

        String questionString = mActivity.getString(R.string.FORTUNE_TELLER_ANSWER, goodsString, stateString, price);
        showAlertDialogMessage(questionString, getString(R.string.FORTUNE_TELLER));
    }

    public void showFortuneTellerNotAvailable() {
        showAlertDialogMessage(getString(R.string.FORTUNE_TELLER_ALSSEP), getString(R.string.FORTUNE_TELLER));
    }
}
