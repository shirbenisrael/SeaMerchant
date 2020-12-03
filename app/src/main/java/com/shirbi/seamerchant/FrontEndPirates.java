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
}
