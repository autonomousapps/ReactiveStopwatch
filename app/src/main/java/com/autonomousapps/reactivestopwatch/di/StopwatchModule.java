package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.time.StopwatchImpl;
import com.autonomousapps.reactivestopwatch.time.SystemTimeProvider;
import com.autonomousapps.reactivestopwatch.time.TimeProvider;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class StopwatchModule {

    @Binds
    public abstract TimeProvider bindsTimeProvider(SystemTimeProvider systemTimeProvider);

    @Binds
    public abstract Stopwatch bindsStopwatch(StopwatchImpl stopwatch);
}