package com.shirbi.seamerchant;

import android.widget.ImageView;
import android.widget.TextView;

public class FrontEndFortuneTeller extends FrontEndGeneric {

    public FrontEndFortuneTeller(MainActivity activity) {
        super(activity);
    }

    public void showInitialMessage() {
        State state = mLogic.mFortuneTellerInformation.state;
        Goods goods = mLogic.mFortuneTellerInformation.goods;

        String stateString = getStateString(state);
        String goodsString = getGoodsString(goods);

        String questionString = mActivity.getString(R.string.FORTUNE_TELLER_QUESTION, goodsString, stateString);
        ((TextView)findViewById(R.id.fortune_teller_message)).setText(questionString);

        ((ImageView)findViewById(R.id.fortune_teller_goods_image)).setImageResource(goods.toMainImageId());
        ((ImageView)findViewById(R.id.fortune_teller_flag_image)).setImageResource(state.toFlagId());

        mActivity.playSound(R.raw.special_offer);
    }

    public void showFortune() {
        String stateString = getStateString(mLogic.mFortuneTellerInformation.state);
        String goodsString = getGoodsString(mLogic.mFortuneTellerInformation.goods);
        int price = mLogic.mFortuneTellerInformation.price;

        mActivity.playSound(R.raw.up);

        String questionString = mActivity.getString(R.string.FORTUNE_TELLER_ANSWER, goodsString, stateString, price);
        showAlertDialogMessage(questionString, getString(R.string.FORTUNE_TELLER), R.drawable.crystal_ball);
    }

    public void showFortuneTellerNotAvailable() {
        mActivity.playSound(R.raw.cannot);
        showAlertDialogMessage(getString(R.string.FORTUNE_TELLER_ALSSEP), getString(R.string.FORTUNE_TELLER), R.drawable.crystal_ball);
    }
}
