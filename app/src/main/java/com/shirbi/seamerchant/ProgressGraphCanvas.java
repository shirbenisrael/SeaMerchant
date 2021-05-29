package com.shirbi.seamerchant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ProgressGraphCanvas extends View {
    public ProgressGraphCanvas(Context cxt, AttributeSet attrs) {
        super(cxt, attrs);
        setMinimumHeight(100);
        setMinimumWidth(100);
        mStartTime = System.currentTimeMillis();
        postInvalidate();
    }

    public Logic mLogic;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int ANIMATION_MILLISECONDS = 3000;
    public static final int PAUSE_ANIMATION_MILLISECONDS = 2000;
    long mStartTime;

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLogic == null || mLogic.mNumProgressSamples == 0) {
            return;
        }

        long elapsedTime = System.currentTimeMillis() - mStartTime;

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);

        long minHeight = mLogic.mProgressSamples[0];
        long maxHeight = minHeight;
        int numSamples = mLogic.mNumProgressSamples;
        for (int i = 1; i < numSamples; i++) {
            minHeight = Math.min(minHeight, mLogic.mProgressSamples[i]);
            maxHeight = Math.max(maxHeight, mLogic.mProgressSamples[i]);
        }

        int canvasWidth = getWidth();
        int canvasHeight = getHeight();
        int sampleWidth = canvasWidth / numSamples;

        int currentStep = (int)(elapsedTime % (ANIMATION_MILLISECONDS + PAUSE_ANIMATION_MILLISECONDS));
        currentStep = Math.min(currentStep, ANIMATION_MILLISECONDS);
        int maxX = canvasWidth * currentStep / ANIMATION_MILLISECONDS;

        int currentHour = Logic.START_HOUR;
        for (int i = 0; i < numSamples-1; i++) {
            long startY = canvasHeight - ((mLogic.mProgressSamples[i] - minHeight) * canvasHeight / (maxHeight - minHeight + 1));
            long endY = canvasHeight -  ((mLogic.mProgressSamples[i+1] - minHeight) * canvasHeight / (maxHeight - minHeight + 1));
            int startX = sampleWidth * i;
            int endX = startX + sampleWidth;

            if (currentHour == Logic.SLEEP_TIME) {
                paint.setColor(Color.RED);
                int locationX = startX + sampleWidth / 2;
                canvas.drawLine(locationX, 0, locationX, canvasHeight, paint);
                currentHour = Logic.START_HOUR;
            } else {
                currentHour++;
            }

            if (startX <= maxX) {
                paint.setColor(Color.GREEN);
                if (endX <= maxX) {
                    canvas.drawLine(startX, startY, endX, endY, paint);
                } else {
                    float slope = (float)(endY - startY) / sampleWidth;
                    canvas.drawLine(startX, startY, maxX, startY + (slope * (maxX - startX)), paint);
                }
            }
        }

        postInvalidateDelayed(1000 / FRAMES_PER_SECOND);
    }
}
