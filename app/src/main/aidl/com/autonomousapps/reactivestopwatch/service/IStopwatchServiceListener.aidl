package com.autonomousapps.reactivestopwatch.service;

interface IStopwatchServiceListener {

    oneway void onTick(long millis);
}
