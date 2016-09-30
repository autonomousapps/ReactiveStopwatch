package com.autonomousapps.reactivestopwatch.service;

interface IStopwatchTickListener {

    oneway void onTick(long millis);
}
