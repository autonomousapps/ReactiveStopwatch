package com.autonomousapps.reactivestopwatch.time;

import javax.inject.Inject;

public class SystemTimeProvider implements TimeProvider {

    @Inject
    public SystemTimeProvider() {
    }

    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
