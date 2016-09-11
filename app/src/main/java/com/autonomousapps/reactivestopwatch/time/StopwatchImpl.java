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

public class StopwatchImpl implements Stopwatch {

    static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private Scheduler scheduler = Schedulers.computation();

    private final List<Lap> laps = new ArrayList<>();

    private boolean isReset = false;
    private boolean isPaused = false;

    private long startTime = 0L;
    private long pausedTime = 0L;

    private final TimeProvider timeProvider;

    @Inject
    public StopwatchImpl(@NonNull TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @NonNull
    @Override
    public Observable<Long> start() {
        isReset = false;
        startTime = timeProvider.now();

        return Observable.interval(1, TIME_UNIT, scheduler)
                .onBackpressureDrop()
                .takeWhile(ignored -> !isReset)
                .map(ignored -> timeProvider.now() - startTime)
                .filter(ignored -> isNotPaused());
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
    public void reset() {
        isReset = true;
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