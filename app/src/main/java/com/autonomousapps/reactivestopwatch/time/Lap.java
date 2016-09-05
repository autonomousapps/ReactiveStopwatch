package com.autonomousapps.reactivestopwatch.time;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Lap {

    public static Lap create(long duration, long endTime) {
        return new AutoValue_Lap(duration, endTime);
    }

    abstract long duration();

    abstract long endTime();
}