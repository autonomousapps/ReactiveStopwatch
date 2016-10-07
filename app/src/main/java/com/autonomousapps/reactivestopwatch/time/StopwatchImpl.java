package com.autonomousapps.reactivestopwatch.time;

import com.autonomousapps.common.LogUtil;

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

    private static final String TAG = StopwatchImpl.class.getSimpleName();

    static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private Scheduler scheduler = Schedulers.computation();

    private final List<Lap> laps = new ArrayList<>();

    private Observable<Long> timerObservable = null;
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
        if (timerObservable != null) {
            LogUtil.d(TAG, "start(). Returning original Observable");
            return timerObservable;
        }

        LogUtil.d(TAG, "start(). Returning new Observable");

        startTime = timeProvider.now();

        // Using Observable.interval() to produce events as fast as possible
        timerObservable = Observable.interval(1, TIME_UNIT, scheduler)
                .onBackpressureDrop()
                .takeUntil(stop)
                .filter(ignored -> isNotPaused())
                .map(ignored -> timeProvider.now() - startTime);
        
        return timerObservable;
    }

    private boolean isNotPaused() {
        return !isPaused;
    }

    @Override
    public void togglePause() {
        LogUtil.d(TAG, "togglePause()");

        isPaused = !isPaused;
        if (isPaused) {
            pausedTime = timeProvider.now();
        } else {
            startTime += timeProvider.now() - pausedTime;
        }
    }

    @Override
    public boolean isPaused() {
        LogUtil.d(TAG, "isPaused()=%s", isPaused);

        return isPaused;
    }

    @Override
    public void reset() {
        LogUtil.d(TAG, "reset()");

        isPaused = false;
        stop.onNext(null);
        timerObservable = null;
    }

    // TODO do I need the list of laps?
    @NonNull
    @Override
    public Lap lap() {
        LogUtil.d(TAG, "lap()");

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