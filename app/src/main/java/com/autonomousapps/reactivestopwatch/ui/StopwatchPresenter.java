package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.time.StopwatchImpl;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StopwatchPresenter implements StopwatchMvp.Presenter {

    private static final String TAG = StopwatchPresenter.class.getSimpleName();

    private final Stopwatch stopwatch;

    private StopwatchMvp.View view;

    private Subscription stopwatchSubscription = null;

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
    }

    @Override
    public void detachView() {
        Log.d(TAG, "detachView()");
        view = null;
    }

    @Override
    public void startOrPause() {
        Log.d(TAG, "startOrPause()");

        if (stopwatchSubscription != null) {
            togglePause();
        } else {
            start();
        }
    }

    @Override
    public void start() {
        Log.d(TAG, "start()");

        stopwatchSubscription = stopwatch.start()
                .takeWhile(ignored -> getView() != null) // TODO use takeUntil()?
                .onBackpressureDrop()
                .subscribeOn(subscribingScheduler)
                .observeOn(observingScheduler)
//                .doOnSubscribe(() -> getView().onTick(0L))
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
                        getView().onTick(tick);
                        request(1L);
                    }
                });

        view.onStopwatchStarted(); // TODO test this
    }

    @Override
    public void togglePause() {
        Log.d(TAG, "togglePause()");

        // TODO I'm not sure about this method returning a value. Either tracking in the presenter or having an isPaused() method in the stopwatch might be better
        boolean isPaused = stopwatch.togglePause();

        // TODO test this
        if (isPaused) {
            view.onStopwatchPaused();
        } else {
            view.onStopwatchStarted();
        }
    }

    @Override
    public void reset() {
        stopwatch.reset();
        if (stopwatchSubscription != null) { // TODO test this
            stopwatchSubscription.unsubscribe();
            stopwatchSubscription = null;
        }
        view.onStopwatchPaused();
    }

    StopwatchMvp.View getView() {
        return view;
    }

    @VisibleForTesting
    void setTestScheduler(@NonNull Scheduler scheduler) {
        subscribingScheduler = scheduler;
        observingScheduler = scheduler;
    }
}