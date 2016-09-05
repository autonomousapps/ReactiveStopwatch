package com.autonomousapps.reactivestopwatch.time;

import org.junit.Test;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class SystemTimeProviderTest {

    private SystemTimeProvider systemTimeProvider = new SystemTimeProvider();

    @Test
    public void timeProviderHasMillisecondPrecision() throws Exception {
        long tolerance = 1L;
        long wait = 10L;

        int iterations = 100;
        long durationSum = 0L;
        for (int i = 0; i < iterations; i++) {
            long first = systemTimeProvider.now();
            Thread.sleep(wait);
            long second = systemTimeProvider.now();
            durationSum += second - first;
        }

        assertThat(durationSum / iterations - wait, lessThanOrEqualTo(tolerance));
    }
}