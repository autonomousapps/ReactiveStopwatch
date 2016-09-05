package com.autonomousapps.reactivestopwatch.time;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class Stopwatch {

    static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private Subscription subscription;
    private Scheduler scheduler = Schedulers.computation();

    private boolean isPaused = false;
    private long pausedTime = 0L;

    public Stopwatch() {
    }

    public void start(@NonNull Action1<Long> action) {
        subscription = Observable.interval(1, TIME_UNIT, scheduler)
                .map(tick -> tick + 1 - getPausedTime())
                .filter(ignored -> isNotPaused())
                .onBackpressureDrop()
                .subscribeOn(scheduler)
                .observeOn(scheduler)
                .subscribe(action);
    }

    private boolean isNotPaused() {
        return !isPaused;
    }

    private long getPausedTime() {
        return isPaused ? ++pausedTime : pausedTime;
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public void reset() {
        subscription.unsubscribe();
    }

    public void lap() {
        // TODO
    }

    @VisibleForTesting
    void setScheduler(@NonNull Scheduler testScheduler) {
        scheduler = testScheduler;
    }
}