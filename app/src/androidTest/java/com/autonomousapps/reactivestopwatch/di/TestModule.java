package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.ui.StopwatchMvp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
class TestModule {

    private StopwatchMvp.Presenter stopwatchPresenter;

    TestModule() {
        stopwatchPresenter = mock(StopwatchMvp.Presenter.class);
    }

    @Singleton
    @Provides
    StopwatchMvp.Presenter providesStopwatchPresenter() {
        return stopwatchPresenter;
    }
}