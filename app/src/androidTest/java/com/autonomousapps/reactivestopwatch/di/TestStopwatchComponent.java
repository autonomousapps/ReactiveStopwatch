package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.ui.StopwatchFragment;
import com.autonomousapps.reactivestopwatch.ui.StopwatchFragmentTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestModule.class)
public interface TestStopwatchComponent extends StopwatchComponent {

    void inject(StopwatchFragment fragment);

    void inject(StopwatchFragmentTest test);
}