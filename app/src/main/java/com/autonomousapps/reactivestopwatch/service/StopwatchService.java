package com.autonomousapps.reactivestopwatch.service;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.time.StopwatchImpl;
import com.autonomousapps.reactivestopwatch.time.SystemTimeProvider;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

public class StopwatchService extends Service {

    private static final String TAG = StopwatchService.class.getSimpleName();

    // TODO inject
    private Stopwatch stopwatch;

    @Override
    public void onCreate() {
        super.onCreate();
        stopwatch = new StopwatchImpl(new SystemTimeProvider());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IStopwatchService.Stub binder = new IStopwatchService.Stub() {

        @Override
        public void start(IStopwatchServiceListener listener) throws RemoteException {
            // TODO implement fully
            stopwatch.start()
                    .subscribe(tick -> {
                        try {
                            listener.onTick(tick);
                        } catch (RemoteException e) {
                            // TODO
                            Log.e(TAG, "RemoteException calling listener::onTick: " + e.getLocalizedMessage());
                        }
                    });
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
            // TODO implement
        }

        @Override
        public void lap() throws RemoteException {
            // TODO implement
        }
    };
}