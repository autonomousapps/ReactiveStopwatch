package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.view.TimeTeller;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public interface StopwatchMvp {

    interface View {

        void onTick(long tick);

        void onStopwatchStarted();

        void onStopwatchPaused();

        /*
         * Manual injection for testing
         */
        @VisibleForTesting
        void setTimeTeller(@NonNull TimeTeller timeTeller);
    }

    interface Presenter {

        void attachView(@NonNull View view);

        void detachView();

        void start();

        void startOrPause();

        void togglePause();

        void reset();
    }
}