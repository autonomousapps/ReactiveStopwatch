package com.autonomousapps.reactivestopwatch.time;

import org.junit.Test;

import static org.junit.Assert.*;

public class LapTest {

    private static final long DURATION = 1000L;
    private static final long END_TIME = 0L;

    @Test
    public void testLap() {
        Lap lap = Lap.create(DURATION, END_TIME);

        assertEquals(DURATION, lap.duration());
        assertEquals(END_TIME, lap.endTime());
    }
}