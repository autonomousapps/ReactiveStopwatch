package com.autonomousapps.reactivestopwatch.time;

/**
 * This class exists to provide a no-op implementation of several methods.
 */
@SuppressWarnings("WeakerAccess") // Mockito needs this to be public
public abstract class AbstractStopwatch implements Stopwatch {

    @Override
    public void onUiShown() {
        // no-op by default
    }

    @Override
    public void onUiHidden() {
        // no-op by default
    }

    @Override
    public void close() {
        // no-op by default
    }
}
