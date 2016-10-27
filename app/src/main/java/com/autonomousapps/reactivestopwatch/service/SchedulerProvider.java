package com.autonomousapps.reactivestopwatch.service;

import android.support.annotation.NonNull;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SchedulerProvider {

    private static final SchedulerProvider INSTANCE = new SchedulerProvider();

    private SchedulerProvider() {
    }

    private Scheduler computationScheduler = Schedulers.computation();

    public static Scheduler getComputationScheduler() {
        return INSTANCE.computationScheduler;
    }

    public static void setComputationScheduler(@NonNull Scheduler scheduler) {
        INSTANCE.computationScheduler = scheduler;
    }
}