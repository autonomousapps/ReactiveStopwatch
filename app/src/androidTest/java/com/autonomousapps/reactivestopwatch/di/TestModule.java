package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.ui.StopwatchMvp;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static com.autonomousapps.reactivestopwatch.di.RxModule.COMPUTATION_SCHEDULER;
import static com.autonomousapps.reactivestopwatch.di.RxModule.MAIN_THREAD_SCHEDULER;
import static com.autonomousapps.reactivestopwatch.di.StopwatchModule.LOCAL_STOPWATCH;
import static com.autonomousapps.reactivestopwatch.di.StopwatchModule.REMOTE_STOPWATCH;
import static org.mockito.Mockito.mock;

@Module
class TestModule {

    private final StopwatchMvp.Presenter mockStopwatchPresenter;
    private final Stopwatch mockLocalStopwatch;
    private final Stopwatch mockRemoteStopwatch;

    private final Scheduler testComputationScheduler = Schedulers.test();
    private final Scheduler testMainThreadScheduler = Schedulers.test();

    TestModule() {
        mockStopwatchPresenter = mock(StopwatchMvp.Presenter.class);
        mockLocalStopwatch = mock(Stopwatch.class);
        mockRemoteStopwatch = mock(Stopwatch.class);
    }

    @Provides
    StopwatchMvp.Presenter providesStopwatchPresenter() {
        return mockStopwatchPresenter;
    }

    @Provides
    @Named(LOCAL_STOPWATCH)
    Stopwatch providesLocalStopwatch() {
        return mockLocalStopwatch;
    }

    @Provides
    @Named(REMOTE_STOPWATCH)
    Stopwatch providesRemoteStopwatch() {
        return mockRemoteStopwatch;
    }

    @Provides
    @Named(COMPUTATION_SCHEDULER)
    Scheduler providesComputationScheduler() {
        return testComputationScheduler;
    }

    @Provides
    @Named(MAIN_THREAD_SCHEDULER)
    Scheduler providesMainThreadScheduler() {
        return testMainThreadScheduler;
    }
}