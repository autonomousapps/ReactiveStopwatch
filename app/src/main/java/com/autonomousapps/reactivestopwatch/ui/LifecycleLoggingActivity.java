package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.common.LogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.autonomousapps.common.LogUtil.asString;

public abstract class LifecycleLoggingActivity extends Activity {

    private static final String TAG = LifecycleLoggingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate(%s)", asString(savedInstanceState));
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        log("onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("onResume()");
    }

    @Override
    protected void onPause() {
        log("onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        log("onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        log("onDestroy()");
        super.onDestroy();
    }

    private static void log(@NonNull String msg, @Nullable Object... args) {
        LogUtil.v(TAG, msg, args);
    }
}