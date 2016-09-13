package com.autonomousapps.reactivestopwatch.view;

public interface TimeTeller {

    /**
     * Sets the current time to {@param timeInMillis}.
     */
    void tellTime(long timeInMillis);
}