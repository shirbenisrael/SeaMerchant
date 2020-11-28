package com.shirbi.seamerchant;

import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

public class FrontEndGeneric {
    MainActivity mActivity;
    Logic mLogic;

    protected  <T extends View> T findViewById(@IdRes int id) {
        return mActivity.getWindow().findViewById(id);
    }

    protected final String getString(@StringRes int resId) {
        return mActivity.getString(resId);
    }

    public FrontEndGeneric(MainActivity activity) {
        mActivity = activity;
        mLogic = activity.mLogic;
    }

    protected Point getWindowSize() {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    void putObjectOnRelativeLayout(View object, float x, float y, float width, float height,
                                   RelativeLayout relativeLayout, Point layoutSize) {

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams((int)(width * layoutSize.x), (int)(height * layoutSize.y));

        params.leftMargin = (int)(x * layoutSize.x);
        params.topMargin = (int)(y * layoutSize.y);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        relativeLayout.removeView(object);
        relativeLayout.addView(object, params);
    }
}
