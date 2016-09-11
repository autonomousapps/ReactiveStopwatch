package com.autonomousapps.reactivestopwatch.time;

public interface TimeProvider {

    /**
     * @return The current time, in milliseconds
     */
    long now();
}