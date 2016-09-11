package com.autonomousapps.reactivestopwatch.time;

import android.support.annotation.NonNull;

import rx.Observable;

public interface Stopwatch {

    @NonNull
    Observable<Long> start();

    void togglePause();

    void reset();

    @NonNull
    Lap lap();
}