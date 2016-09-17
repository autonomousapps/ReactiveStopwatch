package com.autonomousapps.reactivestopwatch.di;

import android.support.annotation.NonNull;

public enum DaggerUtil {
    INSTANCE;

    private StopwatchComponent stopwatchComponent;

    DaggerUtil() {
        if (stopwatchComponent == null) {
            stopwatchComponent = DaggerStopwatchComponent.create();
        }
    }

    public StopwatchComponent getStopwatchComponent() {
        return stopwatchComponent;
    }

    public void setTestComponent(@NonNull StopwatchComponent testComponent) {
        stopwatchComponent = testComponent;
    }
}