package com.autonomousapps.reactivestopwatch.service;

import com.autonomousapps.reactivestopwatch.service.IStopwatchTickListener;

interface IStopwatchService {

    oneway void start(in IStopwatchTickListener listener);

    oneway void togglePause();

    boolean isPaused();

    oneway void reset();

    oneway void lap();

    oneway void close();
}