package com.shirbi.seamerchant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    void putObjectOnRelativeLayout(View object, float x, float y, float width, float height, Point layoutSize) {

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams((int)(width * layoutSize.x), (int)(height * layoutSize.y));

        params.leftMargin = (int)(x * layoutSize.x);
        params.topMargin = (int)(y * layoutSize.y);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        object.setLayoutParams(params);
    }

    public void showAlertDialogMessage(String message, String title) {
        showAlertDialogMessage(message, title, R.drawable.boat_right);
    }

    public void showAlertDialogMessage(String message, String title, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setIcon(icon);

        AlertDialog dialog = builder.create();
        //dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialog.getWindow().getAttributes();
        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setTextSize(mActivity.getResources().getDimension(R.dimen.info_message_size) /
                mActivity.getResources().getDisplayMetrics().density);

        Button btn1 = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn1.setTextSize(mActivity.getResources().getDimension(R.dimen.info_message_confirm_button_size) /
                mActivity.getResources().getDisplayMetrics().density);
    }

    public void timerBlinked(FrontEndTimer timer, int countDown) {
        throw new IllegalStateException("Unexpected timer blink");
    }
}
