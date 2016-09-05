package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.ui.StopwatchMvp;
import com.autonomousapps.reactivestopwatch.ui.StopwatchPresenter;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class StopwatchModule {

    @Binds
    public abstract StopwatchMvp.Presenter bindStopwatchPresenter(StopwatchPresenter stopwatchPresenter);
}