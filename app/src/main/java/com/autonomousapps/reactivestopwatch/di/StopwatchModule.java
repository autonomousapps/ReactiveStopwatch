package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.time.RemoteStopwatch;
import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.time.StopwatchImpl;
import com.autonomousapps.reactivestopwatch.time.SystemTimeProvider;
import com.autonomousapps.reactivestopwatch.time.TimeProvider;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        ContextModule.class
})
public abstract class StopwatchModule {

    public static final String LOCAL_STOPWATCH = "local";
    public static final String REMOTE_STOPWATCH = "remote";

    @Binds
    public abstract TimeProvider bindsTimeProvider(SystemTimeProvider systemTimeProvider);

    @Binds
    @Named(LOCAL_STOPWATCH)
    public abstract Stopwatch bindsLocalStopwatch(StopwatchImpl stopwatch);

    @Binds
    @Named(REMOTE_STOPWATCH)
    public abstract Stopwatch bindsRemoteStopwatch(RemoteStopwatch stopwatch);
}