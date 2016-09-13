package com.autonomousapps.reactivestopwatch.time;

import android.support.annotation.NonNull;

import rx.Observable;

public interface Stopwatch {

    /**
     * Starts the stopwatch.
     *
     * @return an Observable with each item emitted being the time, in milliseconds, since the stopwatch was started.
     */
    @NonNull
    Observable<Long> start();

    /**
     * Pauses or unpauses, depending on the current state of the stopwatch.
     *
     * @return true if the stopwatch is paused; false otherwise.
     */
    boolean togglePause();

    /**
     * Resets the stopwatch.
     */
    void reset();

    /**
     * Calculates a {@link Lap} with duration and end time.
     *
     * @return the lap just created.
     */
    @NonNull
    Lap lap();
}