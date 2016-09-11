package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.time.StopwatchImpl;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class StopwatchPresenter implements StopwatchMvp.Presenter {

    private static final String TAG = StopwatchPresenter.class.getSimpleName();

    private final Stopwatch stopwatch;

    private StopwatchMvp.View view;

    private final PublishSubject<Long> tickPublisher = PublishSubject.create();

    private Scheduler subscribingScheduler = Schedulers.computation();
    private Scheduler observingScheduler = AndroidSchedulers.mainThread();

    @Inject
    public StopwatchPresenter(@NonNull StopwatchImpl stopwatch) {
        this.stopwatch = stopwatch;
    }

    @Override
    public void attachView(@NonNull StopwatchMvp.View view) {
        Log.d(TAG, "attachView()");

        this.view = view;
        tickPublisher.asObservable()
                .takeWhile(ignored -> this.view != null)
                .onBackpressureDrop()
                .observeOn(observingScheduler)
                .doOnSubscribe(() -> this.view.onTick(0L))
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Long tick) {
                        view.onTick(tick);
                        request(1L);
                    }
                });
    }

    @Override
    public void detachView() {
        Log.d(TAG, "detachView()");

        view = null;
    }

    @Override
    public void start() {
        Log.d(TAG, "start()");

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