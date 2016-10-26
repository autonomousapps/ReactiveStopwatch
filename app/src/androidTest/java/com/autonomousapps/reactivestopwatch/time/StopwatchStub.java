package com.autonomousapps.reactivestopwatch.time;

import android.support.annotation.NonNull;

import rx.Observable;

public class StopwatchStub implements Stopwatch {

    @Override
    public void onUiShown() {

    }

    @Override
    public void onUiHidden() {

    }

    @NonNull
    @Override
    public Observable<Long> start() {
        return Observable.just(1L);
    }

    @Override
    public void togglePause() {

    }

    @Override
    public boolean isPaused() {
        return true;
    }

    @Override
    public void reset() {

    }

    @NonNull
    @Override
    public Lap lap() {
        return null;
    }

    @Override
    public void close() {

    }
}