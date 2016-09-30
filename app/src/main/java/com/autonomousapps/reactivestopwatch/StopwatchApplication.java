package com.autonomousapps.reactivestopwatch;

import com.autonomousapps.reactivestopwatch.di.ContextModule;
import com.autonomousapps.reactivestopwatch.di.DaggerStopwatchComponent;
import com.autonomousapps.reactivestopwatch.di.DaggerUtil;
import com.autonomousapps.reactivestopwatch.di.StopwatchComponent;

import android.app.Application;
import android.util.Log;

public class StopwatchApplication extends Application {

    private static final String TAG = StopwatchApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        initDagger();
    }

    private void initDagger() {
        StopwatchComponent component = DaggerStopwatchComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
        DaggerUtil.INSTANCE.setComponent(component);
    }
}