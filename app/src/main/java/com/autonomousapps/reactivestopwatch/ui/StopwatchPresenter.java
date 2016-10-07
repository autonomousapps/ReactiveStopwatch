package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.mvp.ViewNotAttachedException;
import com.autonomousapps.reactivestopwatch.time.Lap;
import com.autonomousapps.reactivestopwatch.time.Stopwatch;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@UiThread
public class StopwatchPresenter implements StopwatchMvp.Presenter {

    private static final String TAG = StopwatchPresenter.class.getSimpleName();

    private final Stopwatch stopwatch;

    private StopwatchMvp.View view;

    private Subscription stopwatchSubscription = null;

    private Scheduler subscribingScheduler = Schedulers.computation();
    private Scheduler observingScheduler = AndroidSchedulers.mainThread();

    @Inject
    StopwatchPresenter(@NonNull @Named("remote") Stopwatch stopwatch) {
        this.stopwatch = stopwatch;
    }

    @Override
    public void attachView(@NonNull StopwatchMvp.View view) {
        Log.d(TAG, "attachView()");
        this.view = view;
        stopwatch.onUiShown();
    }

    @Override
    public void detachView() {
        Log.d(TAG, "detachView()");
        view = null;
        stopwatch.onUiHidden();
    }

    @Override
    public void startOrStop() {
        Log.d(TAG, "startOrStop()");

        if (stopwatchSubscription == null) {
            start();
        } else {
            togglePause();
        }
    }

    private void start() {
        Log.d(TAG, "start()");

        getView().onStopwatchStarted();

        stopwatchSubscription = stopwatch.start()
                .onBackpressureDrop()
                .subscribeOn(subscribingScheduler)
                .observeOn(observingScheduler)
                .filter(ignored -> isAttached())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long tick) {
                        getView().onTick(tick);
                        request(1L); // "reactive pull backpressure". See https://github.com/ReactiveX/RxJava/wiki/Backpressure
                    }
                });
    }

    private void togglePause() {
        Log.d(TAG, "togglePause()");

        stopwatch.togglePause();

        if (stopwatch.isPaused()) {
            getView().onStopwatchStopped();
        } else {
            getView().onStopwatchStarted();
        }
    }

    @Override
    public void resetOrLap() {
        Log.d(TAG, "resetOrLap()");

        if (isRunning()) {
            lap();
        } else {
            reset();
        }
    }

    private boolean isRunning() {
        return stopwatchSubscription != null && !stopwatch.isPaused();
    }

    private void reset() {
        stopwatch.reset();
        if (stopwatchSubscription != null) {
            stopwatchSubscription.unsubscribe();
            stopwatchSubscription = null;
        }
        getView().onStopwatchReset();
    }

    private void lap() {
        Lap lap = stopwatch.lap();
        getView().onNewLap(lap);
    }

    @NonNull
    private StopwatchMvp.View getView() {
        if (view == null) {
            throw new ViewNotAttachedException();
        }
        return view;
    }

    private boolean isAttached() {
        return view != null;
    }

    @VisibleForTesting
    void setTestScheduler(@NonNull Scheduler scheduler) {
        subscribingScheduler = scheduler;
        observingScheduler = scheduler;
    }
}