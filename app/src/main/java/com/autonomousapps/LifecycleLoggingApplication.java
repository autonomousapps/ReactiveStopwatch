package com.autonomousapps;

import com.autonomousapps.common.LogUtil;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class LifecycleLoggingApplication extends Application {

    private static final String TAG = LifecycleLoggingApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        log("onCreate()");
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