package com.autonomousapps.reactivestopwatch.service;

import com.autonomousapps.reactivestopwatch.di.DaggerUtil;
import com.autonomousapps.reactivestopwatch.time.Lap;
import com.autonomousapps.reactivestopwatch.time.Stopwatch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

public class StopwatchService extends Service {

    private static final String TAG = StopwatchService.class.getSimpleName();

    @Inject
    @Named("local")
    Stopwatch stopwatch;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        DaggerUtil.INSTANCE.getStopwatchComponent().inject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IStopwatchService.Stub binder = new IStopwatchService.Stub() {

        private IStopwatchTickListener listener;

        @Override
        public void start(IStopwatchTickListener listener) throws RemoteException {
            this.listener = listener;

            stopwatch.start().subscribe(this::onTick);
        }

        private void onTick(long tick) {
            try {
                listener.onTick(tick);
            } catch (RemoteException e) {
                // TODO
                Log.e(TAG, "RemoteException calling listener::onTick: " + e.getLocalizedMessage());
            }
        }

        @Override
        public void togglePause() throws RemoteException {
            stopwatch.togglePause();
        }

        @Override
        public boolean isPaused() throws RemoteException {
            return stopwatch.isPaused();
        }

        @Override
        public void reset() throws RemoteException {
            stopwatch.reset();
        }

        @Override
        public void lap() throws RemoteException {
            Lap lap = stopwatch.lap();
            // TODO implement: needs to return a value
        }
    };
}