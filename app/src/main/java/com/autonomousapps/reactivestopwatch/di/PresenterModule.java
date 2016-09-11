package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.ui.StopwatchMvp;
import com.autonomousapps.reactivestopwatch.ui.StopwatchPresenter;

import android.support.annotation.NonNull;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        StopwatchModule.class
})
public /*abstract*/ class PresenterModule {

//    @Binds
//    public abstract StopwatchMvp.Presenter bindStopwatchPresenter(StopwatchPresenter stopwatchPresenter);

    @Provides
    public StopwatchMvp.Presenter providesStopwatchPresenter(@NonNull Stopwatch stopwatch) {
        return new StopwatchPresenter(stopwatch);
    }
}