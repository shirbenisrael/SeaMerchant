package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FrontEndSail extends FrontEndGeneric {
    public FrontEndSail(MainActivity activity) {
        super(activity);
    }

    private Point getWindowSize() {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private Point getMapSize() {
        Point mapSize = getWindowSize(); // need to adjust y

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.map_container);
        float weight = ((LinearLayout.LayoutParams)relativeLayout.getLayoutParams()).weight;

        LinearLayout sailLayout = findViewById(R.id.sail_layout);
        float totalWeights = 0;
        for (int i = 0 ; i < sailLayout.getChildCount(); i++) {
            View view = sailLayout.getChildAt(i);
            totalWeights += ((LinearLayout.LayoutParams)view.getLayoutParams()).weight;
        }

        mapSize.y *= weight / totalWeights;

        return mapSize;
    }

    public void initSailRoute() {
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.map_container);
        View destinationAnchor = findViewById(R.id.destination);

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        Point mapSize = getMapSize();
        int windowWidth = mapSize.x;
        int windowHeight = mapSize.y;

        params.leftMargin = (int)(mLogic.mSail.mDestination.toLocationX() * windowWidth);
        params.topMargin = (int)(mLogic.mSail.mDestination.toLocationY() * windowHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        relativeLayout.removeView(destinationAnchor);
        relativeLayout.addView(destinationAnchor, params);
    }
}
