package com.autonomousapps.reactivestopwatch.time;

public class SystemTimeProvider implements TimeProvider {

    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
