package com.autonomousapps.reactivestopwatch.di;

import android.support.annotation.NonNull;

public enum DaggerUtil {
    INSTANCE;

    private StopwatchComponent stopwatchComponent;

    @NonNull
    public StopwatchComponent getStopwatchComponent() {
        checkNotNull();
        return stopwatchComponent;
    }

    private void checkNotNull() {
        if (stopwatchComponent == null) {
            throw new IllegalStateException("StopwatchComponent is null. setComponent() must be called first!");
        }
    }

    public void setComponent(@NonNull StopwatchComponent component) {
        stopwatchComponent = component;
    }
}