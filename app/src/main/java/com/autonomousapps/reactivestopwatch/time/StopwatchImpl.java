package com.autonomousapps.reactivestopwatch.time;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class StopwatchImpl extends AbstractStopwatch {

    static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private Scheduler scheduler = Schedulers.computation();

    private final List<Lap> laps = new ArrayList<>();

    private final PublishSubject<Void> stop = PublishSubject.create();
    private volatile boolean isPaused = false;

    private volatile long startTime = 0L;
    private long pausedTime = 0L;

    private final TimeProvider timeProvider;

    @Inject
    StopwatchImpl(@NonNull TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @NonNull
    @Override
    public Observable<Long> start() {
        startTime = timeProvider.now();

        // Using Observable.interval() to produce events as fast as possible
        return Observable.interval(1, TIME_UNIT, scheduler)
                .onBackpressureDrop()
                .takeUntil(stop)
                .filter(ignored -> isNotPaused())
                .map(ignored -> timeProvider.now() - startTime);
    }

    private boolean isNotPaused() {
        return !isPaused;
    }

    @Override
    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pausedTime = timeProvider.now();
        } else {
            startTime += timeProvider.now() - pausedTime;
        }
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void reset() {
        isPaused = false;
        stop.onNext(null);
    }

    // TODO do I need the list of laps?
    @NonNull
    @Override
    public Lap lap() {
        long lastEndTime = 0L;
        int lastIndex = laps.size() - 1;
        if (lastIndex >= 0) {
            Lap lastLap = laps.get(lastIndex);
            lastEndTime = lastLap.endTime();
        }
        long now = timeProvider.now();
        Lap lap = Lap.create(now - lastEndTime, now);
        laps.add(lap);
        return lap;
    }

    @VisibleForTesting
    void setScheduler(@NonNull Scheduler testScheduler) {
        scheduler = testScheduler;
    }
}