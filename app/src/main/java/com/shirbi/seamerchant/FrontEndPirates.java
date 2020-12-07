package com.shirbi.seamerchant;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

        findViewById(R.id.attack_pirates_layout).setBackgroundResource(R.drawable.battle);
    }

    private void updateGoodsOffer(Goods goods) {
        LinearLayout inventoryLayout = findViewById(R.id.inventory_for_pirates);
        String goodString = getString(goods.toStringId());
        SeekBar seekBar = (SeekBar)inventoryLayout.getChildAt(goods.getValue() * 2 + 1);

        int maxUnits = mLogic.mInventory[goods.getValue()];
        String goodToGive = mActivity.getString(R.string.GOODS_TO_OFFSET, goodString, seekBar.getProgress(), maxUnits);

        TextView textView = (TextView)inventoryLayout.getChildAt(goods.getValue() * 2);
        textView.setText(goodToGive);
    }

    private void updateCashOffer() {
        TextView textView = findViewById(R.id.negotiate_cash_text);
        SeekBar seekBar = findViewById(R.id.negotiate_cash_seek_bar);
        int maxUnits = mLogic.mCash;
        String cashToGive = mActivity.getString(R.string.CASH_TO_OFFER, seekBar.getProgress(), maxUnits);
        textView.setText(cashToGive);
    }

    public void showNegotiatePirates() {
        LinearLayout inventoryLayout = findViewById(R.id.inventory_for_pirates);
        for (Goods goods : Goods.values()) {
            SeekBar seekBar = (SeekBar)inventoryLayout.getChildAt(goods.getValue() * 2 + 1);
            int maxUnits = mLogic.mInventory[goods.getValue()];
            seekBar.setMax(maxUnits);
            seekBar.setProgress(maxUnits / 2);

            updateGoodsOffer(goods);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int indexView = ((ViewGroup) seekBar.getParent()).indexOfChild(seekBar);
                        updateGoodsOffer(Goods.values()[indexView/2]);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {}
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        SeekBar seekBar = findViewById(R.id.negotiate_cash_seek_bar);
        int maxUnits = mLogic.mCash;
        seekBar.setMax(maxUnits);
        seekBar.setProgress(maxUnits / 2);

        updateCashOffer();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int indexView = ((ViewGroup) seekBar.getParent()).indexOfChild(seekBar);
                    updateCashOffer();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public boolean sendOfferToPirates() {
        LinearLayout inventoryLayout = findViewById(R.id.inventory_for_pirates);
        int[] goodToOffer = new int[Goods.NUM_GOODS_TYPES];
        for (Goods goods : Goods.values()) {
            SeekBar seekBar = (SeekBar)inventoryLayout.getChildAt(goods.getValue() * 2 + 1);
            goodToOffer[goods.getValue()] = seekBar.getProgress();
        }

        SeekBar seekBar = findViewById(R.id.negotiate_cash_seek_bar);
        int cashToOffer = seekBar.getProgress();

        return mLogic.mSail.sendOfferToPirates(goodToOffer, cashToOffer);
    }

    public void showPiratesRefuseOffer() {
        ((TextView)findViewById(R.id.pirates_title)).setText(R.string.PIRATES_REFUSE_OFFER);
        ((TextView)findViewById(R.id.pirates_chances_text_view)).setText(R.string.PIRATES_MUST_ATTACK);

        findViewById(R.id.escape_button).setVisibility(View.GONE);
        findViewById(R.id.negotiate_button).setVisibility(View.GONE);
        findViewById(R.id.pirates_tip).setVisibility(View.INVISIBLE);
    }
}
