package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.ui.StopwatchMvp;
import com.autonomousapps.reactivestopwatch.ui.StopwatchPresenter;

import dagger.Binds;
import dagger.Module;

@Module(includes = {
        StopwatchModule.class
})
public abstract class PresenterModule {

    @Binds
    public abstract StopwatchMvp.Presenter bindStopwatchPresenter(StopwatchPresenter stopwatchPresenter);
}