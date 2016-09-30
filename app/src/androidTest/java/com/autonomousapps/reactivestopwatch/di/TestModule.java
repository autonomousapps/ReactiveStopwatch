package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.ui.StopwatchMvp;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
class TestModule {

    private final StopwatchMvp.Presenter mockStopwatchPresenter;
    private final Stopwatch mockStopwatch;

    TestModule() {
        mockStopwatchPresenter = mock(StopwatchMvp.Presenter.class);
        mockStopwatch = mock(Stopwatch.class);
    }

    @Provides
    StopwatchMvp.Presenter providesStopwatchPresenter() {
        return mockStopwatchPresenter;
    }

    @Provides
    @Named("local")
    Stopwatch providesLocalStopwatch() {
        return mockStopwatch;
    }
}