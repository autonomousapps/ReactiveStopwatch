package com.autonomousapps.reactivestopwatch.time;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class Stopwatch {

    static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private Scheduler scheduler = Schedulers.computation();

    private final List<Lap> laps = new ArrayList<>();

    private boolean isReset = false;
    private boolean isPaused = false;
    private long pausedTime = 0L;
    private long currentTime = 0L;

    public Stopwatch() {
    }

    public Observable<Long> start() {
        isReset = false;

        return Observable.interval(1, TIME_UNIT, scheduler)
                .takeWhile(ignored -> !isReset)
                .map(tick -> currentTime = tick + 1 - getPausedTime())
                .filter(ignored -> isNotPaused())
                .onBackpressureDrop();
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
        isReset = true;
    }

    // TODO do I need the list of laps?
    public Lap lap() {
        long lastEndTime = 0L;
        int lastIndex = laps.size() - 1;
        if (lastIndex >= 0) {
            Lap lastLap = laps.get(lastIndex);
            lastEndTime = lastLap.endTime();
        }
        Lap lap = Lap.create(currentTime - lastEndTime, currentTime);
        laps.add(lap);
        return lap;
    }

    @VisibleForTesting
    void setScheduler(@NonNull Scheduler testScheduler) {
        scheduler = testScheduler;
    }
}