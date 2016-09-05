package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class StopwatchModule {

    @Provides
    public Stopwatch providesStopwatch() {
        return new Stopwatch();
    }
}