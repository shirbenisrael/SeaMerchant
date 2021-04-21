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

import java.text.DecimalFormat;

public class FrontEndGeneric {
    static final int MAX_SEEK_BAR_UNITS = (1000 * 1000 * 1000 * 2);

    MainActivity mActivity;
    Logic mLogic;
    protected DecimalFormat mDecimalFormat;

    protected  <T extends View> T findViewById(@IdRes int id) {
        return mActivity.getWindow().findViewById(id);
    }

    protected final String getString(@StringRes int resId) {
        return mActivity.getString(resId);
    }

    public FrontEndGeneric(MainActivity activity) {
        mActivity = activity;
        mLogic = activity.mLogic;
        mDecimalFormat = new DecimalFormat("#,###");
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

    public void showErrorMessage(String message, String title) {
        mActivity.playSound(R.raw.cannot);
        showAlertDialogMessage(message, title);
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

    public void startNewGameAfterTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle("סיום הדרכה");
        builder.setMessage("ההדרכה הסתימה. בהצלחה במסחר. היזהר מסכנות!");
        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mActivity.startNewGame();
            }
        });
        builder.setIcon( R.drawable.boat_right);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
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

    protected void disableButton(View view) {
        view.setAlpha(0.3f);
        view.setEnabled(false);
    }

    protected void enableButton(View view) {
        view.setAlpha(1.0f);
        view.setEnabled(true);
    }

    protected void setViewEnable(View view, boolean isEnable) {
        if (isEnable) {
            enableButton(view);
        } else {
            disableButton(view);
        }
    }

    protected void setViewEnable(@IdRes int id, boolean isEnable) {
        setViewEnable(findViewById(id), isEnable);
    }

    protected void disableButton(@IdRes int id) {
        setViewEnable(id, false);
    }

    protected void enableButton(@IdRes int id) {
        setViewEnable(id, true);
    }

    protected String getStateString(State state) {
        return mActivity.getString(state.toStringId());
    }

    protected String getGoodsString(Goods goods) {
        return mActivity.getString(goods.toStringId());
    }
}
