package com.autonomousapps.reactivestopwatch.service;

import com.autonomousapps.common.LogUtil;
import com.autonomousapps.reactivestopwatch.di.DaggerUtil;
import com.autonomousapps.reactivestopwatch.time.Lap;
import com.autonomousapps.reactivestopwatch.time.Stopwatch;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.TestScheduler;
import rx.subscriptions.CompositeSubscription;

import static com.autonomousapps.reactivestopwatch.di.RxModule.COMPUTATION_SCHEDULER;
import static com.autonomousapps.reactivestopwatch.di.StopwatchModule.LOCAL_STOPWATCH;

public class StopwatchService extends LifecycleLoggingService {

    private static final String TAG = StopwatchService.class.getSimpleName();

    @Inject
    @Named(LOCAL_STOPWATCH)
    Stopwatch stopwatch;

//    @Inject
//    @Named(COMPUTATION_SCHEDULER)
    Scheduler computationScheduler;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerUtil.INSTANCE.getStopwatchComponent().inject(this);
        computationScheduler = SchedulerProvider.getComputationScheduler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY; // TODO which?
    }

    @Override
    public void onDestroy() {
        subscriptions.clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * If {@link #stopwatch} is no longer running and all clients are unbound, stop service.
     */
    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        if (!isRunning) {
            LogUtil.v(TAG + "_lifecycle", "stopSelf()");
            stopSelf();
        }

        return false; // False by default. True calls onRebind in future
    }

    void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    private final Observable.Transformer<Long, Long> schedulingTransformer = observable ->
            observable.observeOn(computationScheduler);

    private final IStopwatchService.Stub binder = new IStopwatchService.Stub() {

        private IStopwatchTickListener listener;

        @Override
        public void start(IStopwatchTickListener listener) throws RemoteException {
            this.listener = listener;

            Subscription subscription = stopwatch.start()
                    .compose(schedulingTransformer)
                    .subscribe(this::onTick);
            subscriptions.add(subscription);

            setIsRunning(true);
        }

        private void onTick(long tick) {
            try {
                listener.onTick(tick);
            } catch (RemoteException e) {
                // TODO: more robust error handling
                LogUtil.e(TAG, "RemoteException calling listener::onTick: " + e.getLocalizedMessage());
            }
        }

        @Override
        public void togglePause() throws RemoteException {
            stopwatch.togglePause(); // TODO not idempotent. Should do nothing if not started/paused
            setIsRunning(!isPaused());
        }

        @Override
        public boolean isPaused() throws RemoteException {
            return stopwatch.isPaused();
        }

        @Override
        public void reset() throws RemoteException {
            stopwatch.reset();
            setIsRunning(false);
        }

        @Override
        public Lap lap() throws RemoteException {
            return stopwatch.lap();
        }

        @Override
        public void close() {
            stopwatch.close();
        }
    };
}