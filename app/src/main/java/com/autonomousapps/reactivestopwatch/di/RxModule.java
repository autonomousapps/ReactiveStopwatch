package com.autonomousapps.reactivestopwatch.di;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class RxModule {

    public static final String COMPUTATION_SCHEDULER = "computation";
    public static final String MAIN_THREAD_SCHEDULER = "main_thread";

    @Provides
    @Named(COMPUTATION_SCHEDULER)
    Scheduler providesComputationScheduler() {
        return Schedulers.computation();
    }

    @Provides
    @Named(MAIN_THREAD_SCHEDULER)
    Scheduler providesMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }
}