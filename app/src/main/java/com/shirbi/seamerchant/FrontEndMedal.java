package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

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
            if( medal.getValue() % 5 == 0) {
                medalImage.setBackgroundResource(R.drawable.medal_red);
            } else {
                medalImage.setBackgroundResource(R.drawable.medal_gray);
            }

            Button medalText = (Button)((LinearLayout)scroll.getChildAt(medal.getValue())).getChildAt(1);
            medalText.setText(medal.toString());
            medalText.setLayoutParams(new LinearLayout.LayoutParams(5 * windowSize.x / 6, windowSize.x / 6));
        }
    }

}
