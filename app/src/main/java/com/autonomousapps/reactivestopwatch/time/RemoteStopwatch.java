package com.autonomousapps.reactivestopwatch.time;

import com.autonomousapps.common.LogUtil;
import com.autonomousapps.reactivestopwatch.service.IStopwatchService;
import com.autonomousapps.reactivestopwatch.service.IStopwatchTickListener;
import com.autonomousapps.reactivestopwatch.service.StopwatchService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

// Inspiration from http://www.donnfelker.com/rxjava-with-aidl-services/
// This also represents an implementation of the Adapter pattern.
public class RemoteStopwatch implements Stopwatch {

    private static final String TAG = RemoteStopwatch.class.getSimpleName();

    private final Context context;

    private IStopwatchService remoteService;
    private BehaviorSubject<IStopwatchService> serviceConnectionSubject = BehaviorSubject.create();

    private CompositeSubscription subscriptions;

    @Inject
    RemoteStopwatch(@NonNull Context context) {
        this.context = context;
        subscriptions = new CompositeSubscription();
        startService();
    }

    // I want the service to continue running even when unbound, so I start it first
    private void startService() {
        Intent serviceIntent = new Intent(context, StopwatchService.class);
        context.startService(serviceIntent);
    }

    @Override
    public void onUiShown() {
        Intent serviceIntent = new Intent(context, StopwatchService.class);
        context.bindService(serviceIntent, remoteServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onUiHidden() {
        context.unbindService(remoteServiceConnection);
    }

    private final ServiceConnection remoteServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected()");
            remoteService = IStopwatchService.Stub.asInterface(service);

//            serviceConnectionSubject = BehaviorSubject.create(); // TODO can this happen without the prior subject completing first?
            serviceConnectionSubject.onNext(remoteService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "onServiceDisconnected()");
            remoteService = null;
            serviceConnectionSubject.onCompleted(); // TODO do I actually want to complete? What about re-connections?
        }
    };

    @NonNull
    @Override
    public Observable<Long> start() {
        final PublishSubject<Long> tickPublisher = PublishSubject.create();

        Subscription subscription = serviceConnectionSubject.subscribe(
                // The action to take `onNext`, i.e., when `onServiceConnected` is called.
                remoteStopwatchService -> {
                    try {
                        // Start the remote stopwatch, which is owned by the remote Service
                        // Passing in the listener for `tick` events to communicate back across process boundaries.
                        remoteStopwatchService.start(new IStopwatchTickListener.Stub() {

                            @Override
                            public void onTick(long tick) throws RemoteException {
                                tickPublisher.onNext(tick);
                            }
                        });
                    } catch (RemoteException e) {
                        tickPublisher.onError(e);
                    }
                }, tickPublisher::onError/*, tickPublisher::onCompleted*/);

        subscriptions.add(subscription);
        return tickPublisher.asObservable();
    }

    @Override
    public void togglePause() {
        try {
            // TODO null-check
            remoteService.togglePause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPaused() {
        try {
            // TODO null-check
            return remoteService.isPaused();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false; // TODO correct default?
    }

    @Override
    public void reset() {
        try {
            // TODO null-check
            remoteService.reset();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Lap lap() {
        try {
            remoteService.lap();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // TODO implement
        return null;
    }

    @Override
    public void close() { // AutoCloseable
        try {
            // TODO null-check
            remoteService.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        context.unbindService(remoteServiceConnection);
        if (subscriptions != null) {
            subscriptions.clear();
        }
    }
}