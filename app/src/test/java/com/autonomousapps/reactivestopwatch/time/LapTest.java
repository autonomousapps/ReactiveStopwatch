package com.autonomousapps.reactivestopwatch.time;

import org.junit.Test;

import static org.junit.Assert.*;

public class LapTest {

    @Test
    public void testLap() {
        Lap lap = Lap.create(1000L, 0L);

        assertEquals(1000L, lap.duration());
        assertEquals(0L, lap.endTime());
    }

}