package com.autonomousapps.reactivestopwatch.time;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.any;

public class SystemTimeProviderTest {

    @Test
    public void nowReturnsALong() throws Exception {
        SystemTimeProvider timeProvider = new SystemTimeProvider();

        Assert.assertThat(timeProvider.now(), any(Long.class));
    }
}