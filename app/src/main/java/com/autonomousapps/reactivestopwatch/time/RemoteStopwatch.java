package com.autonomousapps.reactivestopwatch.time;

import com.autonomousapps.reactivestopwatch.service.ServiceProxy;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.Observable;

// Inspiration from http://www.donnfelker.com/rxjava-with-aidl-services/
// This also represents an implementation of the Adapter pattern.
public class RemoteStopwatch implements Stopwatch {

    private final ServiceProxy serviceProxy;

    @Inject
    RemoteStopwatch(@NonNull ServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
        serviceProxy.startService();
    }

    @Override
    public void onUiShown() {
        serviceProxy.bindService();
    }

    @Override
    public void onUiHidden() {
        serviceProxy.unbindService();
    }

    @NonNull
    @Override
    public Observable<Long> start() {
        return serviceProxy.start();

    }

    @Override
    public void togglePause() {
        serviceProxy.togglePause();
    }

    @Override
    public boolean isPaused() {
        return serviceProxy.isPaused();
    }

    @Override
    public void reset() {
        serviceProxy.reset();
    }

    @NonNull
    @Override
    public Lap lap() {
        return serviceProxy.lap();
    }

    @Override
    public void close() { // AutoCloseable
        serviceProxy.close();
    }
}