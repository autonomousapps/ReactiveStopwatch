package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.service.StopwatchService;
import com.autonomousapps.reactivestopwatch.ui.StopwatchFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        PresenterModule.class/*,
        RxModule.class*/
})
public interface StopwatchComponent {

    void inject(StopwatchFragment stopwatchFragment);

    void inject(StopwatchService service);
}