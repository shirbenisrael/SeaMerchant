package com.shirbi.seamerchant;

import android.view.View;
import android.widget.TextView;

public class FrontEndPirates extends FrontEndGeneric {

    public FrontEndPirates(MainActivity activity) {
        super(activity);
    }

    public void showChances() {
        ((TextView)findViewById(R.id.pirates_title)).setText(R.string.PIRATES);

        Sail sail = mLogic.mSail;
        String whatToDo = getString(R.string.WHAT_TO_DO_WITH_PIRATES);
        String numGuardsString = sail.mSelectedNumGuardShips == 0 ?
                getString(R.string.NO_GUARD_SHIPS) :
                mActivity.getString(R.string.NUM_GUARDS_AGAINST_PIRATES, sail.mSelectedNumGuardShips);
        String chancesString = mActivity.getString(R.string.PIRATES_CHANCES_WIN_ESCAPE, sail.getPercentsToWinPirates(),
                sail.getPercentsToEscapeFromPirates());

        String finalString = whatToDo + " " + numGuardsString + " " + chancesString;

        ((TextView)findViewById(R.id.pirates_chances_text_view)).setText(finalString);

        findViewById(R.id.escape_button).setVisibility(View.VISIBLE);
        findViewById(R.id.negotiate_button).setVisibility(View.VISIBLE);
        findViewById(R.id.pirates_tip).setVisibility(View.VISIBLE);
    }

    public void showFailEscapeMessage() {
        ((TextView)findViewById(R.id.pirates_title)).setText(R.string.PIRATES_FAST);
        ((TextView)findViewById(R.id.pirates_chances_text_view)).setText(R.string.PIRATES_MUST_ATTACK);

        findViewById(R.id.escape_button).setVisibility(View.GONE);
        findViewById(R.id.negotiate_button).setVisibility(View.GONE);
        findViewById(R.id.pirates_tip).setVisibility(View.INVISIBLE);
    }

    public void showWinPiratesMessage() {
        Sail sail = mLogic.mSail;

        ((TextView) findViewById(R.id.battle_result_title)).setText(R.string.WIN_THE_BATTLE);

        if (sail.mBattleResult == Sail.BattleResult.WIN_AND_CAPTURE) {
            String result = mActivity.getString(R.string.WIN_CAPACITY, sail.mPiratesCapacity);
            ((TextView) findViewById(R.id.battle_result)).setText(result);

            findViewById(R.id.attack_pirates_layout).setBackgroundResource(R.drawable.capture);
        } else {
            String result = mActivity.getString(R.string.WIN_TREASURE, sail.mPiratesTreasure);
            ((TextView) findViewById(R.id.battle_result)).setText(result);

            findViewById(R.id.attack_pirates_layout).setBackgroundResource(R.drawable.treasure);
        }

        if (sail.mPiratesDamage > 0) {
            String damage = mActivity.getString(R.string.WIN_WITH_DAMAGE, sail.mPiratesDamage);
            ((TextView) findViewById(R.id.battle_damage)).setText(damage);
            findViewById(R.id.battle_damage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.battle_damage).setVisibility(View.GONE);
        }

    }

    public void showLoseToPiratesMessage() {
        Sail sail = mLogic.mSail;
        ((TextView)findViewById(R.id.battle_result_title)).setText(R.string.PIRATES);

        String stolen;
        if (sail.mIsPirateStoleGoods) {
            String goodString = getString(sail.mPiratesStolenGoods.toStringId());
            stolen = mActivity.getString(R.string.LOSE_BATTLE_AND_GOODS, sail.mPiratesStolen, goodString);
        } else {
            stolen = mActivity.getString(R.string.LOSE_BATTLE_AND_CASH, sail.mPiratesStolen);
        }
        ((TextView)findViewById(R.id.battle_result)).setText(stolen);

        String damage = mActivity.getString(R.string.LOSE_BATTLE_DAMAGE, sail.mPiratesDamage);
        ((TextView)findViewById(R.id.battle_damage)).setText(damage);
        findViewById(R.id.battle_damage).setVisibility(View.VISIBLE);

        findViewById(R.id.attack_pirates_layout).setBackgroundResource(R.drawable.battle);
    }

    public void showPiratesRefuseOffer() {
        ((TextView)findViewById(R.id.pirates_title)).setText(R.string.PIRATES_REFUSE_OFFER);
        ((TextView)findViewById(R.id.pirates_chances_text_view)).setText(R.string.PIRATES_MUST_ATTACK);

        findViewById(R.id.escape_button).setVisibility(View.GONE);
        findViewById(R.id.negotiate_button).setVisibility(View.GONE);
        findViewById(R.id.pirates_tip).setVisibility(View.INVISIBLE);
    }
}
