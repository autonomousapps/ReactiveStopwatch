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

    private static final long NOT_PAUSED = -1L;

    private final TimeProvider timeProvider;

    private Subscription subscription;
    private Scheduler scheduler = Schedulers.computation();

    private long startTime = 0L;
    private long pausedTime = NOT_PAUSED;

    public Stopwatch(@NonNull TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public void start(@NonNull Action1<Long> action) {
        startTime = timeProvider.now();

        subscription = Observable.interval(1, TimeUnit.MILLISECONDS)
                .startWith(0L)
                .map(interval -> interval == 0 ? interval : timeProvider.now() - startTime - getPausedTime())
                .filter(ignored -> pausedTime == NOT_PAUSED)
                .onBackpressureDrop()
                .subscribeOn(scheduler)
                .observeOn(scheduler)
                .subscribe(action);
    }

    private long getPausedTime() {
        return pausedTime == NOT_PAUSED ? 0L : pausedTime;
    }

    public void pause() {
        pausedTime = timeProvider.now() - startTime;
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