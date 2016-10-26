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

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class StopwatchService extends LifecycleLoggingService {

    private static final String TAG = StopwatchService.class.getSimpleName();

    @Inject
    @Named("local")
    Stopwatch stopwatch;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerUtil.INSTANCE.getStopwatchComponent().inject(this);
        LogUtil.e(TAG, "onCreate(): stopwatch=%s", stopwatch.toString());
        binder2 = new ServiceStub(stopwatch);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY; // TODO which?
    }

    @Override
    public void onDestroy() {
        subscriptions.clear();
//        binder2 = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder2;
//        return binder;
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

    class ServiceStub extends IStopwatchService.Stub {

        private final Stopwatch localStopwatch;

        private IStopwatchTickListener listener;

        ServiceStub(@NonNull Stopwatch stopwatch) {
            LogUtil.e(TAG, "ServiceStub(): stopwatch=%s", stopwatch.toString());
            localStopwatch = stopwatch;
            LogUtil.e(TAG, "ServiceStub(): localStopwatch=%s", localStopwatch.toString());
        }

        @Override
        public void start(IStopwatchTickListener listener) throws RemoteException {
            this.listener = listener;

            LogUtil.e(TAG, "start(): stopwatch=%s", localStopwatch.toString());

            Subscription subscription = localStopwatch.start().subscribe(this::onTick); // TODO for testing, pass in a Subscriber instead?
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
            localStopwatch.togglePause(); // TODO not idempotent. Should do nothing if not started/paused
            setIsRunning(!isPaused());
        }

        @Override
        public boolean isPaused() throws RemoteException {
            LogUtil.e(TAG, "isPaused(): stopwatch=%s", localStopwatch.toString());
            return localStopwatch.isPaused();
        }

        @Override
        public void reset() throws RemoteException {
            localStopwatch.reset();
            setIsRunning(false);
        }

        @Override
        public Lap lap() throws RemoteException {
            return localStopwatch.lap();
        }

        @Override
        public void close() {
            localStopwatch.close();
        }
    }

    private IStopwatchService.Stub binder2;

//    private final IStopwatchService.Stub binder = new IStopwatchService.Stub() {
//
//        private IStopwatchTickListener listener;
//
//        @Override
//        public void start(IStopwatchTickListener listener) throws RemoteException {
//            this.listener = listener;
//
//            Observable<Long> obs = stopwatch.start();
//            Subscription subscription = obs.subscribe(this::onTick); // TODO for testing, pass in a Subscriber instead?
//            subscriptions.add(subscription);
//
//            setIsRunning(true);
//        }
//
//        private void onTick(long tick) {
//            try {
//                listener.onTick(tick);
//            } catch (RemoteException e) {
//                // TODO: more robust error handling
//                LogUtil.e(TAG, "RemoteException calling listener::onTick: " + e.getLocalizedMessage());
//            }
//        }
//
//        @Override
//        public void togglePause() throws RemoteException {
//            stopwatch.togglePause(); // TODO not idempotent. Should do nothing if not started/paused
//            setIsRunning(!isPaused());
//        }
//
//        @Override
//        public boolean isPaused() throws RemoteException {
//            return stopwatch.isPaused();
//        }
//
//        @Override
//        public void reset() throws RemoteException {
//            stopwatch.reset();
//            setIsRunning(false);
//        }
//
//        @Override
//        public Lap lap() throws RemoteException {
//            return stopwatch.lap();
//        }
//
//        @Override
//        public void close() {
//            stopwatch.close();
//        }
//    };
}