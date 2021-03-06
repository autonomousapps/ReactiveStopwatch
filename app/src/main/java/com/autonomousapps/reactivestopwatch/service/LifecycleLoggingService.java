package com.autonomousapps.reactivestopwatch.service;

import com.autonomousapps.common.LogUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.autonomousapps.common.LogUtil.asString;

public abstract class LifecycleLoggingService extends Service {

    private static final String TAG = LifecycleLoggingService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("onStartCommand(%s, %d, %d)", asString(intent), flags, startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        log("onBind(%s)", asString(intent));
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        log("onUnbind(%s)", asString(intent));
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        log("onRebind(%s)", asString(intent));
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        log("onTrimMemory(%d)", level);
    }

    private static void log(@NonNull String msg, @Nullable Object... args) {
        LogUtil.v(TAG, msg, args);
    }
}