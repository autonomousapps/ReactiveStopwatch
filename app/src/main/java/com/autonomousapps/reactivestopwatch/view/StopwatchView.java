package com.autonomousapps.reactivestopwatch.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StopwatchView extends TextView implements TimeTeller {

    public StopwatchView(Context context) {
        super(context);
    }

    public StopwatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StopwatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public StopwatchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        if (isInEditMode()) {
            return;
        }

        setText("00:00:00.0");
    }

    @Override
    public void tellTime(long timeInMillis) {
        long hh = TimeUnit.MILLISECONDS.toHours(timeInMillis);
        long mm = TimeUnit.MILLISECONDS.toMinutes(timeInMillis - TimeUnit.HOURS.toMillis(hh));
        long ss = TimeUnit.MILLISECONDS.toSeconds(timeInMillis - TimeUnit.HOURS.toMillis(hh) - TimeUnit.MINUTES.toMillis(mm));
        long ds = (timeInMillis - TimeUnit.MILLISECONDS.toSeconds(timeInMillis) * 1000) / 100L;

        String time = String.format(Locale.US, "%02d:%02d:%02d.%01d", hh, mm, ss, ds);

        setText(time);
    }
}
