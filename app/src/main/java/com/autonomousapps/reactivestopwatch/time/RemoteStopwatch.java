package com.autonomousapps.reactivestopwatch.time;

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
public class RemoteStopwatch implements Stopwatch, AutoCloseable {

    private static final String TAG = RemoteStopwatch.class.getSimpleName();

    private final Context context;

    private IStopwatchService remoteService;
    private BehaviorSubject<IStopwatchService> stopwatchSubject = BehaviorSubject.create();

    private CompositeSubscription subscriptions;

    @Inject
    public RemoteStopwatch(@NonNull Context context) {
        this.context = context;

        subscriptions = new CompositeSubscription();

        Intent serviceIntent = new Intent(context, StopwatchService.class);
        context.bindService(serviceIntent, remoteServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection remoteServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IStopwatchService.Stub.asInterface(service);
            stopwatchSubject.onNext(remoteService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteService = null;
            stopwatchSubject.onCompleted(); // TODO do I actually want to complete? What about re-connections?
        }
    };

    @NonNull
    @Override
    public Observable<Long> start() {
        final PublishSubject<Long> tickPublisher = PublishSubject.create();

        Subscription subscription = stopwatchSubject.subscribe(
                // The action to take `onNext`, i.e., when `onServiceConnected` is called.
                remoteStopwatchService -> {
                    try {
                        // Start the remote stopwatch, which is contained by the remote Service
                        // Passing in the listener for `tick` events to communicate back across process boundaries. TODO rename listener
                        remoteStopwatchService.start(new IStopwatchTickListener.Stub() {

                            @Override
                            public void onTick(long tick) throws RemoteException {
                                tickPublisher.onNext(tick);
                            }
                        });
                    } catch (RemoteException e) {
                        tickPublisher.onError(e);
                    }
                }, tickPublisher::onError); // TODO what's the point of this onError handling?
        // TODO `onCompleted` action?

        subscriptions.add(subscription);
        return tickPublisher.asObservable();
    }

    @Override
    public void togglePause() {
        // TODO implement
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
        // TODO implement
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
        // TODO implement
        return null;
    }

    @Override
    public void close() { // AutoCloseable
        // TODO double-check: where's the best place to unsubscribe?
        context.unbindService(remoteServiceConnection);
        if (subscriptions != null) {
            subscriptions.clear();
        }
    }
}