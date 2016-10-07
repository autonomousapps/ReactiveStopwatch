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
abstract class StopwatchModule {

    @Binds
    public abstract TimeProvider bindsTimeProvider(SystemTimeProvider systemTimeProvider);

    @Binds
    @Named("local")
    public abstract Stopwatch bindsLocalStopwatch(StopwatchImpl stopwatch);

    @Binds
    @Named("remote")
    public abstract Stopwatch bindsRemoteStopwatch(RemoteStopwatch stopwatch);
}