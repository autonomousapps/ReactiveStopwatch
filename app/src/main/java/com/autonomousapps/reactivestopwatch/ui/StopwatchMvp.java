package com.autonomousapps.reactivestopwatch.ui;

import android.support.annotation.NonNull;

public interface StopwatchMvp {

    interface View {

        void onTick(long tick);
    }

    interface Presenter {

        void attachView(@NonNull View view);

        void detachView();

        void start();

        void togglePause();

        void reset();
    }
}