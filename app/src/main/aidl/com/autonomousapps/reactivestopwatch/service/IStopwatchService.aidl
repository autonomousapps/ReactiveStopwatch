package com.autonomousapps.reactivestopwatch.service;

import com.autonomousapps.reactivestopwatch.service.IStopwatchTickListener;
import com.autonomousapps.reactivestopwatch.time.Lap;

interface IStopwatchService {

    oneway void start(in IStopwatchTickListener listener);

    oneway void togglePause();

    boolean isPaused();

    oneway void reset();

    Lap lap();

    oneway void close();
}