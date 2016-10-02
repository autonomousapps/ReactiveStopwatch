package com.autonomousapps.reactivestopwatch.time;

import com.google.auto.value.AutoValue;

import android.os.Parcel;
import android.os.Parcelable;

//@AutoValue
public /*abstract */class Lap implements Parcelable {

//    public static Lap create(long duration, long endTime) {
//
//        return new AutoValue_Lap(duration, endTime);
//    }
//
//    abstract long duration();
//
//    abstract long endTime();

    public static Lap create(long duration, long endTime) {
        return new Lap(duration, endTime);
    }

    private final long duration;
    private final long endTime;

    private Lap(long duration, long endTime) {
        this.duration = duration;
        this.endTime = endTime;
    }

    public long duration() {
        return duration;
    }

    public long endTime() {
        return endTime;
    }

    public static final Parcelable.Creator<Lap> CREATOR = new
            Parcelable.Creator<Lap>() {
                public Lap createFromParcel(Parcel in) {
                    return new Lap(in);
                }

                public Lap[] newArray(int size) {
                    return new Lap[size];
                }
            };

    private Lap(Parcel in) {
        this.duration = in.readLong();
        this.endTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(duration);
        out.writeLong(endTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Lap{" +
                "duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Lap lap = (Lap) o;

        if (duration != lap.duration) {
            return false;
        }
        return endTime == lap.endTime;

    }

    @Override
    public int hashCode() {
        int result = (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        return result;
    }
}