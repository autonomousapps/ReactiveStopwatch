package com.autonomousapps.reactivestopwatch.service;

import com.autonomousapps.reactivestopwatch.service.IStopwatchServiceListener;

interface IStopwatchService {

    oneway void start(in IStopwatchServiceListener listener);

    oneway void togglePause();

    boolean isPaused();

    oneway void reset();

    oneway void lap();
}