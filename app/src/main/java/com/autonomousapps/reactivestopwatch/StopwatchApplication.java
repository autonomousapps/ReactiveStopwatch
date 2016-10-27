package com.autonomousapps.reactivestopwatch;

import com.autonomousapps.LifecycleLoggingApplication;
import com.autonomousapps.reactivestopwatch.di.ContextModule;
import com.autonomousapps.reactivestopwatch.di.DaggerStopwatchComponent;
import com.autonomousapps.reactivestopwatch.di.DaggerUtil;
import com.autonomousapps.reactivestopwatch.di.RxModule;
import com.autonomousapps.reactivestopwatch.di.StopwatchComponent;

public class StopwatchApplication extends LifecycleLoggingApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initDagger();
    }

    private void initDagger() {
        StopwatchComponent component = DaggerStopwatchComponent.builder()
                .contextModule(new ContextModule(this))
//                .rxModule(new RxModule())
                .build();
        DaggerUtil.INSTANCE.setComponent(component);
    }
}