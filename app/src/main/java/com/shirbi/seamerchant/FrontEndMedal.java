package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FrontEndMedal extends FrontEndGeneric {
    public FrontEndMedal(MainActivity activity) {
        super(activity);

        LinearLayout scroll = findViewById(R.id.medal_scroll_view);
        for (Medal medal : Medal.values()) {
            LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
            // inflate whatever layout xml has your common xml
            inflater.inflate(R.layout.one_medal_layout, scroll);
        }

        setMedalAppearance();
    }

    private void setMedalAppearance() {
        Point windowSize = getWindowSize();

        LinearLayout scroll = findViewById(R.id.medal_scroll_view);
        for (Medal medal : Medal.values()) {
            Button medalImage = (Button)((LinearLayout)scroll.getChildAt(medal.getValue())).getChildAt(0);
            medalImage.setLayoutParams(new LinearLayout.LayoutParams(windowSize.x / 6, windowSize.x / 6));
            Button medalText = (Button)((LinearLayout)scroll.getChildAt(medal.getValue())).getChildAt(1);
            medalText.setText(getString(medal.getTitle()));
            medalText.setLayoutParams(new LinearLayout.LayoutParams(5 * windowSize.x / 6, windowSize.x / 6));

            setMedalIcon(medal);
        }
    }

    public void showMedalCondition(View medalButton) {
        LinearLayout scroll = findViewById(R.id.medal_scroll_view);
        int medalIndex = scroll.indexOfChild((View)medalButton.getParent());
        Medal medal = Medal.values()[medalIndex];

        showAlertDialogMessage(getString(medal.getCondition()), getString(medal.getTitle()), R.drawable.medal);
    }

    public void showNewMedal(Medal medal) {
        TextView title = findViewById(R.id.medal_title);
        title.setText(getString(medal.getTitle()));

        TextView condition = findViewById(R.id.medal_condition);
        condition.setText(getString(medal.getCondition()));

        setMedalIcon(medal);
    }

    private void setMedalIcon(Medal medal) {
        LinearLayout scroll = findViewById(R.id.medal_scroll_view);

        Button medalImage = (Button)((LinearLayout)scroll.getChildAt(medal.getValue())).getChildAt(0);
        if (mLogic.hasMedal(medal)) {
            medalImage.setBackgroundResource(R.drawable.medal_red);
        } else {
            medalImage.setBackgroundResource(R.drawable.medal_gray);
        }
    }
}
