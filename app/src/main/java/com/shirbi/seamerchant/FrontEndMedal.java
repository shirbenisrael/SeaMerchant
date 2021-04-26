package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

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

    private ProgressBar getMedalProgressBar(Medal medal)
    {
        LinearLayout scroll = findViewById(R.id.medal_scroll_view);
        LinearLayout thisMedalInfo = (LinearLayout)scroll.getChildAt(medal.getValue());
        LinearLayout textAndProgressLayout = (LinearLayout)(thisMedalInfo.getChildAt(1));
        ProgressBar progressBar = (ProgressBar)((textAndProgressLayout).getChildAt(1));
        return progressBar;
    }

    private void setMedalAppearance() {
        Point windowSize = getWindowSize();

        LinearLayout scroll = findViewById(R.id.medal_scroll_view);
        for (Medal medal : Medal.values()) {
            LinearLayout thisMedalInfo = (LinearLayout)scroll.getChildAt(medal.getValue());
            Button medalImage = (Button)(thisMedalInfo.getChildAt(0));
            medalImage.setLayoutParams(new LinearLayout.LayoutParams(windowSize.x / 6, windowSize.x / 6));

            LinearLayout textAndProgressLayout = (LinearLayout)(thisMedalInfo.getChildAt(1));
            Button medalText = (Button)((textAndProgressLayout).getChildAt(0));
            medalText.setText(getString(medal.getTitle()));
            medalText.setLayoutParams(new LinearLayout.LayoutParams(5 * windowSize.x / 6, windowSize.x / 6 * 3 / 4));

            ProgressBar progressBar = getMedalProgressBar(medal);
            progressBar.setLayoutParams(new LinearLayout.LayoutParams(5 * windowSize.x / 6, windowSize.x / 6 * 1 / 4));

            int colorId = ((medal.getBonus() == 0) ? R.color.black : R.color.red);
            medalText.setTextColor(mActivity.getColor(colorId));
            setMedalIcon(medal);
        }
    }

    public void showMedalCondition(View medalButton) {
        LinearLayout scroll = findViewById(R.id.medal_scroll_view);
        int medalIndex = scroll.indexOfChild((View)medalButton.getParent().getParent());
        Medal medal = Medal.values()[medalIndex];

        int medalBonusId = medal.getBonus();
        String condition = getString(medal.getCondition());
        String message;

        if (medalBonusId == 0) {
            message = condition;
        } else {
            String bonus = getString(medalBonusId);
            message = mActivity.getString(R.string.MEDAL_WITH_BONUS, condition, bonus);
        }
        showAlertDialogMessage(message, getString(medal.getTitle()), R.drawable.medal);
    }

    private void putNewObjectOnScreen(Medal medal) {
        @DrawableRes int resource = medal.getIcon();

        RelativeLayout medalScreenLayout = findViewById(R.id.new_medal_layout);
        ImageView imageView = new ImageView(mActivity);
        imageView.setImageResource(resource);
        medalScreenLayout.addView(imageView, 0);
        float locationX = 0.1f + mLogic.generateFloat() * 0.8f;
        float locationY = 0.1f + mLogic.generateFloat() * 0.8f;
        putObjectOnRelativeLayout(imageView, locationX, locationY, 0.2f, 0.2f, getWindowSize());

        FrontEndInflater inflater = new FrontEndInflater(mActivity, 25, 40, 5000);
        inflater.inflate(imageView);
    }

    public void showNewMedal(Medal medal) {
        TextView title = findViewById(R.id.medal_title);
        title.setText(getString(medal.getTitle()));

        TextView condition = findViewById(R.id.medal_condition);
        condition.setText(getString(medal.getCondition()));

        TextView bonus = findViewById(R.id.medal_bonus);
        int  bonusStringId = medal.getBonus();
        String bonusString = (bonusStringId == 0) ? "" :
                mActivity.getString(R.string.ONLY_BONUS, getString(bonusStringId));
        bonus.setText(bonusString);

        setMedalIcon(medal);

        for (int i = 0; i < 40; i++) {
            putNewObjectOnScreen(medal);
        }
    }

    public void setMedalIcon(Medal medal) {
        LinearLayout scroll = findViewById(R.id.medal_scroll_view);

        Button medalImage = (Button)((LinearLayout)scroll.getChildAt(medal.getValue())).getChildAt(0);

        float alpha = 1.0f;

        if (mLogic.hasMedal(medal)) {
            medalImage.setBackgroundResource(R.drawable.medal_red);
        } else {
            medalImage.setBackgroundResource(R.drawable.medal_gray);

            if (!mLogic.canGetThisMedal(medal)) {
                alpha = 0.3f; // set opacity for medal that cannot get int this game.
            }
        }

        medalImage.setAlpha(alpha);
    }

    private void updateMedalProgress(Medal medal)
    {
        ProgressBar progressBar = getMedalProgressBar(medal);
        if (mLogic.hasMedal(medal)) {
            progressBar.setMax(1);
            progressBar.setProgress(1);
            return;
        }

        if (!mLogic.canGetThisMedal(medal)) {
            progressBar.setMax(1);
            progressBar.setProgress(0);
            return;
        }

        progressBar.setMax(100);
        progressBar.setProgress(Math.min(100, (int)(mLogic.getMedalProgress(medal) * 100)));
    }

    public void updateAllMedalImages() {
        for (Medal medal : Medal.values()) {
            setMedalIcon(medal);
            updateMedalProgress(medal);
        }
    }
}
