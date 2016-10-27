package com.autonomousapps.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class LogUtil {

    public static void v(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        Log.v(tag, String.format(msg, args));
    }

    public static void d(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        Log.d(tag, String.format(msg, args));
    }

    public static void w(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        Log.w(tag, String.format(msg, args));
    }

    public static void e(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        Log.e(tag, String.format(msg, args));
    }

    @NonNull
    public static String asString(@Nullable Object obj) {
        return obj != null ? obj.toString() : "null";
    }
}