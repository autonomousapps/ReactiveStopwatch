package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.service.StopwatchService;
import com.autonomousapps.reactivestopwatch.service.StopwatchServiceTest;
import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.ui.StopwatchFragment;
import com.autonomousapps.reactivestopwatch.ui.StopwatchFragmentTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestModule.class)
public interface TestStopwatchComponent extends StopwatchComponent {

    void inject(StopwatchFragmentTest test);

    void inject(StopwatchServiceTest test);

    // TODO remove once bug is fixed
    void inject(StopwatchFragment fragment);

    // TODO remove once bug is fixed
    void inject(StopwatchService service);
}