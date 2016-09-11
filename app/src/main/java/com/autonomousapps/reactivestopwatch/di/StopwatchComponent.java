package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.ui.StopwatchFragment;

import dagger.Component;

@Component(modules = {
        PresenterModule.class
})
public interface StopwatchComponent {

    void inject(StopwatchFragment stopwatchFragment);
}