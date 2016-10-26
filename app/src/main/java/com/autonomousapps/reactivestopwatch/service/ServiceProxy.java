package com.autonomousapps.reactivestopwatch.service;

import com.autonomousapps.common.LogUtil;
import com.autonomousapps.reactivestopwatch.time.Lap;

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

public class ServiceProxy {

    private static final String TAG = ServiceProxy.class.getSimpleName();

    private final Context context;

    private IStopwatchService remoteService;
    private final BehaviorSubject<IStopwatchService> serviceConnectionSubject = BehaviorSubject.create();
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public ServiceProxy(@NonNull Context context) {
        this.context = context;
    }

    public void startService() {
        Intent serviceIntent = new Intent(context, StopwatchService.class);
        context.startService(serviceIntent);
    }

    public void bindService() {
        Intent serviceIntent = new Intent(context, StopwatchService.class);
        context.bindService(serviceIntent, remoteServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        context.unbindService(remoteServiceConnection);
        remoteService = null;
        subscriptions.clear();
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
                        LogUtil.e(TAG, "RemoteException");
                        onError(e);
                    }
                }, this::onError, this::onComplete);

        subscriptions.add(subscription);
        return tickPublisher.asObservable();
    }

    private void onError(Throwable throwable) {
        LogUtil.e(TAG, "onError(): %s", throwable.getLocalizedMessage());
        throwable.printStackTrace();
    }

    private void onComplete() {
        // TODO implement reconnection logic
    }

//    @Override
    public void togglePause() {
        try {
            // TODO null-check
            remoteService.togglePause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

//    @Override
    public boolean isPaused() {
        try {
            // TODO null-check
            return remoteService.isPaused();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false; // TODO correct default?
    }

//    @Override
    public void reset() {
        try {
            // TODO null-check
            remoteService.reset();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @NonNull
//    @Override
    public Lap lap() {
        try {
            // TODO null-check
            return remoteService.lap();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return Lap.BAD_LAP;
    }

//    @Override
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