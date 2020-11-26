package com.shirbi.seamerchant;

import android.view.View;

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
}
