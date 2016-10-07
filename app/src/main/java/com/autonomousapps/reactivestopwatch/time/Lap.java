package com.autonomousapps.reactivestopwatch.time;

import com.google.auto.value.AutoValue;

import android.os.Parcel;
import android.os.Parcelable;

@AutoValue
public abstract class Lap implements Parcelable {

    static final Lap BAD_LAP = create(-1L, -1L);

    public static Lap create(long duration, long endTime) {
        return new AutoValue_Lap(duration, endTime);
    }

    abstract long duration();

    abstract long endTime();

    // Manual Parcelable implementation because auto-value-parcel does not work with AIDL yet.
    // Tools bug: https://code.google.com/p/android/issues/detail?id=224480
    public static final Parcelable.Creator<Lap> CREATOR = new
            Parcelable.Creator<Lap>() {
                public Lap createFromParcel(Parcel in) {
                    return Lap.create(in);
                }

                public Lap[] newArray(int size) {
                    return new Lap[size];
                }
            };

    private static Lap create(Parcel in) {
        return Lap.create(in.readLong(), in.readLong());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(duration());
        out.writeLong(endTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}