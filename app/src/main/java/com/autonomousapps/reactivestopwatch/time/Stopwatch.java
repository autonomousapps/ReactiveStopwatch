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

    private long startTime = 0L;
    private long newPausedTime = 0L;

    private final TimeProvider timeProvider;

    public Stopwatch(@NonNull TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public Observable<Long> start() {
        isReset = false;
        startTime = timeProvider.now();

        return Observable.interval(1, TIME_UNIT, scheduler)
                .onBackpressureDrop()
                .takeWhile(ignored -> !isReset)
//                .map(tick -> currentTime = tick + 1 - getPausedTime())
                .map(ignored -> timeProvider.now() - startTime)
                .filter(ignored -> isNotPaused());
    }

    private boolean isNotPaused() {
        return !isPaused;
    }

    private long getPausedTime() {
        return isPaused ? ++pausedTime : pausedTime;
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            newPausedTime = timeProvider.now();
        } else {
            // start   @ 0:   startTime = 0
            // pause   @ 100: newPausedTime = 100
            // unpause @ 200: now - newPausedTime = 100
            //                startTime = 100
            startTime += timeProvider.now() - newPausedTime;
        }
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
//        Lap lap = Lap.create(currentTime - lastEndTime, currentTime);
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