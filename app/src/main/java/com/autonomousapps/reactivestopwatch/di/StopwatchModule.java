package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.time.SystemTimeProvider;
import com.autonomousapps.reactivestopwatch.time.TimeProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class StopwatchModule {

    @Provides
    public TimeProvider providesTimeProvider() {
        return new SystemTimeProvider();
    }

    @Provides
    public Stopwatch providesStopwatch(TimeProvider timeProvider) {
        return new Stopwatch(timeProvider);
    }
}