package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.time.RemoteStopwatch;
import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.time.SystemTimeProvider;
import com.autonomousapps.reactivestopwatch.time.TimeProvider;

import dagger.Binds;
import dagger.Module;

@Module(includes = {
        ContextModule.class
})
abstract class StopwatchModule {

    @Binds
    public abstract TimeProvider bindsTimeProvider(SystemTimeProvider systemTimeProvider);

//    @Binds
//    public abstract Stopwatch bindsStopwatch(StopwatchImpl stopwatch);

    @Binds
    public abstract Stopwatch bindsStopwatch(RemoteStopwatch stopwatch);
}