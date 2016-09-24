package com.autonomousapps.reactivestopwatch.test;

/**
 * Simple timer for tracking elapsed time, which is measured in milliseconds since instantiation.
 */
public class Timer {

    private final long start;

    public Timer() {
        start = System.currentTimeMillis();
    }

    public long elapsedTime() {
        return System.currentTimeMillis() - start;
    }
}
