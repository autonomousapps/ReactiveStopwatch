package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class StopwatchPresenter implements StopwatchMvp.Presenter {

    private final Stopwatch stopwatch;

    private StopwatchMvp.View view;

    private final PublishSubject<Long> tickPublisher = PublishSubject.create();

    private Scheduler subscribingScheduler = Schedulers.computation();
    private Scheduler observingScheduler = AndroidSchedulers.mainThread();

    @Inject
    public StopwatchPresenter(@NonNull Stopwatch stopwatch) {
        this.stopwatch = stopwatch;
    }

    @Override
    public void attachView(@NonNull StopwatchMvp.View view) {
        this.view = view;
        tickPublisher.asObservable()
                .takeWhile(ignored -> this.view != null)
                .observeOn(observingScheduler)
                .doOnSubscribe(() -> this.view.onTick(0L))
                .subscribe(tick -> this.view.onTick(tick));
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void start() {
        stopwatch.start()
                .subscribeOn(subscribingScheduler)
                .subscribe(this::onTick);
    }

    private void onTick(long tick) {
        tickPublisher.onNext(tick);
    }

    @Override
    public void togglePause() {
        stopwatch.togglePause();
    }

    @Override
    public void reset() {
        stopwatch.reset();
    }

    @VisibleForTesting
    void setTestScheduler(@NonNull Scheduler scheduler) {
        subscribingScheduler = scheduler;
        observingScheduler = scheduler;
    }
}